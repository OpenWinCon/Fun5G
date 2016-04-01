

import json


from flask import Flask, jsonify, request, render_template, redirect
from flask.ext.restful import Api, Resource, reqparse


app = Flask(__name__)
api = Api(app)

config = json.load(open('./config.json', 'r'))

@app.route('/')
def helloworld():
  return render_template('index.html')


@app.route('/list')
def user_list():
  return render_template('userlist.html')

@app.route('/ap/')
def redir():
  return redirect('http://www.naver.com', code=302)
  



if __name__ == '__main__':
  app.run(host='0.0.0.0', port=config['gui_server']['port'],
          debug=True)



