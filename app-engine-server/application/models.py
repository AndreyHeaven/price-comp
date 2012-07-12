from google.appengine.ext import db

class Good(db.Model):
    code = db.StringProperty()

class Price(db.Model):
    good = db.ReferenceProperty(Good)
    accuracy = db.FloatProperty()
    price = db.FloatProperty()
    longitude = db.FloatProperty()
    latitude = db.FloatProperty()
    date = db.DateProperty()
