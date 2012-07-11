from flask import Response
from json import dumps
from application import app

def json_response(values):
    '''This functions gets some list or dict and return in via WSGI in JSON format.'''
    return Response(dumps(values), mimetype='application/json')

def home():
    return json_response({'message': 'Hello, world!'})

@app.route('/good/<id>')
def hernya(id):
    return json_response({'name':id})

def warmup():
    """App Engine warmup handler
    See http://code.google.com/appengine/docs/python/config/appconfig.html#Warming_Requests
    """
    return ''

