# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
#
# Also note: You'll have to insert the output of 'django-admin sqlcustom [app_label]'
# into your database.
from __future__ import unicode_literals

from django.db import models


class ApInformation(models.Model):
    id = models.CharField(db_column='ID', primary_key=True, max_length=17)  # Field name made lowercase.
    description = models.CharField(db_column='Description', max_length=256, blank=True, null=True)  # Field name made lowercase.
    power = models.CharField(db_column='Power', max_length=3, blank=True, null=True)  # Field name made lowercase.
    ssid = models.CharField(db_column='SSID', max_length=256)  # Field name made lowercase.
    ip = models.CharField(db_column='IP', max_length=15)  # Field name made lowercase.
    password = models.CharField(db_column='Password', max_length=256, blank=True, null=True)  # Field name made lowercase.
    channel = models.CharField(db_column='Channel', max_length=2)  # Field name made lowercase.
    broadcast = models.CharField(db_column='Broadcast', max_length=4)  # Field name made lowercase.
    mode = models.CharField(db_column='Mode', max_length=2)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'AP_Information'
