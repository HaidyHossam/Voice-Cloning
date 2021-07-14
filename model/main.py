from os import makedirs
from encoder import inference as encoder
from synthesizer.inference import Synthesizer
from vocoder import inference as vocoder
from pathlib import Path
import numpy as np
import sys
import torch
import librosa
import argparse
import soundfile as sf


def parse_args(parser):
    """
    Parse commandline arguments.
    """
    parser.add_argument('-i', '--input', type=str, required=True,
                        help='String containing text to be synthesized')
    parser.add_argument('-a', '--audio_fpath', type=str, required=True,
                        help='Audio file path to retrieve audio')
    parser.add_argument('-o', '--output',type=Path, default="output\\",
                        help='output folder to save audio')
    parser.add_argument("-e", "--enc_models_dir", type=Path, default="encoder\\saved_models\\pretrained.pt", 
                        help="Directory containing saved encoder models")
    parser.add_argument("-s", "--syn_models_dir", type=Path, default="synthesizer\\saved_models\\pretrained\\pretrained.pt", 
                        help="Directory containing saved synthesizer models")
    parser.add_argument("-v", "--voc_models_dir", type=Path, default="vocoder\\saved_models\\pretrained.pt", 
                        help="Directory containing saved vocoder models")
    parser.add_argument("--cpu", action="store_true", default="true",
                        help="If True, processing is done on CPU, even when a GPU is available.")

    return parser
    
def main():
    parser = argparse.ArgumentParser(
        description='PyTorch Tacotron 2 Inference')
    parser = parse_args(parser)
    args, _ = parser.parse_known_args()

    synthesizer = None # type: Synthesizer
    current_wav = None

    #Initialize models
    init_encoder()
    synthesizer = init_synthesizer()
    init_vocoder()
    print("Initializing models...DONE")

    #Load wav file and preprocess it
    current_wav = Synthesizer.load_preprocess_wav(args.audio_fpath)
    print("Loading wav file....DONE")
    #Calculate wav embedding
    embed = add_real_utterance(current_wav)
    print("Calculating embedding....DONE")
    #Synthesize input text using embedding calculated 
    spec, breaks = synthesize(synthesizer, args.input, embed, seed=None)
    print("Synthesizing input....DONE")
    #Produce wav from the mel-spectogram 
    output_wav = vocode(spec, breaks, seed=None)
    print("Creating output wav....DONE")

    save_audio_file(output_wav, Synthesizer.sample_rate, args.output)

def init_encoder():
    model_fpath = "file_app\\model\\encoder\\saved_models\\pretrained.pt"
    enc_path = Path(model_fpath)
    encoder.load_model(enc_path)

def init_synthesizer():
    model_fpath = "file_app\\model\\synthesizer\\saved_models\\pretrained\\pretrained.pt"
    syn_path = Path(model_fpath)
    synthesizer = Synthesizer(syn_path)
    return synthesizer

def init_vocoder():
    model_fpath = "file_app\\model\\vocoder\\saved_models\\pretrained.pt"
    voc_path = Path(model_fpath)
    vocoder.load_model(voc_path)

def save_audio_file(wav, sample_rate, fpath):        
    if fpath:
        #Default format is wav
        if Path(fpath).suffix == "":
            fpath += ".wav"
        sf.write(fpath, wav, sample_rate)

def add_real_utterance(wav):
    #preprocess chosen wav
    encoder_wav = encoder.preprocess_wav(wav)
    embed, partial_embeds,_ = encoder.embed_utterance(encoder_wav, return_partials=True)

    return embed

def synthesize(synthesizer, textToClone, embed, seed):
    seed = None

    if seed is not None:
        torch.manual_seed(seed)

    texts = textToClone.split("\n")
    print(texts)
    embeds = [embed] * len(texts)
    specs = synthesizer.synthesize_spectrograms(texts, embeds)
    breaks = [spec.shape[1] for spec in specs]
    spec = np.concatenate(specs, axis=1)
    
    return spec, breaks

def vocode(spec, breaks, seed):
    assert spec is not None

    seed = None
    if seed is not None:
        torch.manual_seed(seed)

    wav = vocoder.infer_waveform(spec)
    
    # Add breaks
    b_ends = np.cumsum(np.array(breaks) * Synthesizer.hparams.hop_size)
    b_starts = np.concatenate(([0], b_ends[:-1]))
    wavs = [wav[start:end] for start, end, in zip(b_starts, b_ends)]
    breaks = [np.zeros(int(0.15 * Synthesizer.sample_rate))] * len(breaks)
    wav = np.concatenate([i for w, b in zip(wavs, breaks) for i in (w, b)])

    # Trim excessive silences
    wav = encoder.preprocess_wav(wav)

    wav = wav / np.abs(wav).max() * 0.97

    return wav


if __name__ == '__main__':
    main()