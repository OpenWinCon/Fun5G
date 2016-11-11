from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^$', views.AP_list, name='AP_list'),
]
