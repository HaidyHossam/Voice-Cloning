from django.db import models

class File(models.Model):
    file = models.FileField(blank=False, null=False)
    FileName = models.CharField(max_length=40)
    UserName = models.CharField(max_length=40)
    Text = models.CharField(max_length=500)