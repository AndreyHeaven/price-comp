from google.appengine.ext import db

class Good(db.Model):
    code = db.StringProperty()

class Price(db.Model):
    """Example Model"""
    good = db.ReferenceProperty(Good)
    accurace = db.FloatProperty()
    price = db.FloatProperty()
    longitude = db.FloatProperty()
    latitude = db.FloatProperty()
    price = db.FloatProperty()