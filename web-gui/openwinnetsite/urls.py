from django.conf.urls import patterns, include, url
from django.conf.urls.static import static
from django.contrib import admin
from django.contrib.staticfiles.urls import staticfiles_urlpatterns
from openwinnetsite import settings
from django.conf.urls import url, include
from rest_framework import routers

urlpatterns = [
    # Examples:
    # url(r'^$', 'openwinnetsite.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    url(r'', include('AP.urls')),
    url(r'^admin/', include(admin.site.urls)),
    url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework')),
]
urlpatterns += staticfiles_urlpatterns()
