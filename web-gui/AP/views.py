import subprocess

from django.shortcuts import render, render_to_response
from django.http import HttpResponse
from django.template import Context, loader
from .models import ApInformation
from rest_framework import status
from rest_framework import viewsets
from rest_framework.decorators import api_view
from rest_framework.response import Response
from django.views.decorators.csrf import csrf_exempt


cmd = '/home/dan/project/controller/command'

def process_method(IP, SSID, chSS, PW, chPW, CH, chCH, BROAD, chBC):
        if chSS != '':
            if SSID != chSS:
                subprocess.Popen([cmd, IP, 'ssid', chSS])
        if chPW != '':
            if PW != chPW:
                subprocess.Popen([cmd, IP, 'password', chPW])
        else:
            if PW != chPW:
                subprocess.Popen([cmd, IP, 'password'])
        if chCH != '':
            if CH != chCH:
                subprocess.Popen([cmd, IP, 'channel', chCH])
        if chBC != '':
            if BROAD != chBC:
                subprocess.Popen([cmd, IP, 'hide', chBC])

@csrf_exempt
def AP_list(request):
    if request.method == 'GET':
        all_ap_list = ApInformation.objects.all();
        t = loader.get_template('AP/index.html')
        c = Context({
            'all_ap_list': all_ap_list,
        })
        return HttpResponse(t.render(c))
    elif request.method == "POST":
        if request.POST["START"] != '':
            IP = request.POST["IP"]
            opt = request.POST["START"]
            subprocess.Popen([cmd, IP, opt])
        else:
            process_method(request.POST["IP"], request.POST["SSID"], request.POST["chSSID"],
                            request.POST["PW"], request.POST["chPW"], request.POST["CH"], request.POST["chCH"],
                            request.POST["BC"], request.POST["chBC"])

        all_ap_list = ApInformation.objects.all();
        t = loader.get_template('AP/index.html')
        c = Context({
            'all_ap_list': all_ap_list,
        })
        return HttpResponse(t.render(c))
