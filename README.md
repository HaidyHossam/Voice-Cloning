# Voice-Cloning
Android application that allows users to clone voices given any sample voice and text using Tacotron2
# Samples
Open 
# Install Requirements

**Python 3.6 or 3.7** 

* Download this repository.
* Install [PyTorch](pip install torch==1.8.0 torchvision==0.9.0 torchaudio==0.8.0).
* Install [ffmpeg](https://ffmpeg.org/download.html#get-packages).
* Run `pip install -r requirements.txt` to install the remaining necessary packages.

# Usage

python main.py -i  "This is a test to the model" -a samples/audio_0.wav -o  output4
-i: Text input
-a: Input Sample Path
-o: Output Path/Filename
