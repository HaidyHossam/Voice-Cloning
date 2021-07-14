from django.conf.urls import url
from .views import FileView

urlpatterns = [
    url('upload/$', FileView.as_view(), name='file-upload'),
    url('upload/(?P<file_name>\w+)/$', FileView.as_view(), name='file-upload'),

]
