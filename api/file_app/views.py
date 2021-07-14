from rest_framework.views import APIView
from rest_framework.parsers import MultiPartParser, FormParser
from rest_framework.response import Response
from rest_framework import status
from .models import File
from .serializers import FileSerializer
from django.http import HttpResponse
from django.core.files.base import ContentFile


# from .model import main
import os


class FileView(APIView):
    parser_classes = (MultiPartParser, FormParser)

    def post(self, request, *args, **kwargs):
        data = request.data
        text = data['Text']
        input_file = data['file']
        output_file_name = data['FileName']

        ooutput_directory = "E:/GP/api/media/"
        #run model , save output as "output_file_name".mp3 inside output_directory (choose a directory for your outputs and change the variable)
        
        os.system('python file_app\model\main.py -i  '+ text +'-a file_app/model/samples/audio_0.wav -o' + output_directory + output_file_name)

        output_file_path = f'{output_directory}/{output_file_name}.mp3'
        with open(output_file_path, 'rb')as f:
            output_file = ContentFile(f.read(), name=output_file_name+'.mp3')

        data['file'] = output_file
        file_serializer = FileSerializer(data=data)
        try:
            if file_serializer.is_valid():
                file_serializer.save()
                return self.prepare_file_response(file_serializer.data['file'], output_file_name)
            else:
                return Response(file_serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        except Exception as e:
            print(e)

    def get(self, request, **kwargs):
        Audios = File.objects.filter(FileName=self.kwargs['file_name'])
        try:
            serializer = FileSerializer(Audios, many=True)
            path = serializer.data[0]['file']
        except Exception as e:
            return Response(status=status.HTTP_404_NOT_FOUND)

        response = self.prepare_file_response(path, "test")
        return response

    def prepare_file_response(self, filepath, filename):
        try:
            with open(self.get_full_filepath(filepath), 'rb')as f:
                file_data = f.read()
                response = HttpResponse(
                    file_data, content_type='application/mpeg')
                response['Content-Disposition'] = f'attachement;filename ="{filename}.mp3"'
        except Exception as e:
            print(e)
        return response

    def get_full_filepath(self, path):
        base_dir = 'E:/GP/api/'
        filepath = os.path.join(base_dir, path.split("/")
                                [1]+'/'+path.split("/")[2])
        return filepath
