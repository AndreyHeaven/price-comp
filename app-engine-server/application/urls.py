from flask import render_template

from application import app
from application import views


#app.add_url_rule('/add_price/', view_func=views.add_price, methods=['PUT'])
#app.add_url_rule('/add_store/', view_func=views.add_store, methods=['PUT'])
#app.add_url_rule('/hernya', view_func=views.hernya, methods=['GET',]) # Main page

## URL dispatch rules
# App Engine warm up handler
# See http://code.google.com/appengine/docs/python/config/appconfig.html#Warming_Requests
#app.add_url_rule('/_ah/warmup', 'warmup', view_func=views.warmup)

## Error handlers
# Handle 404 errors
# @app.errorhandler(404)
# def page_not_found(e):
#     return render_template('404.html'), 404

# Handle 500 errors
# @app.errorhandler(500)
# def server_error(e):
#     return render_template('500.html'), 500

