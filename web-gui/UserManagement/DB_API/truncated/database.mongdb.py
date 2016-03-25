import pymongo
import socket


class DBHandler():
  def __init__(self, config):
    self.connected = False
    self.config = config
    self.connect_db(config)

  def connect_db(self, config):
    if self.connected:   # For singleton
      return

    host = config['db_server']['host']
    port = config['db_server']['port']

    try:
      self.connection = pymongo.MongoClient(host, port)
    except pymongo.errors.ConnectionFailure:
      raise Exception(
          "Need a MongoDB server running on {}, port {}".format(host, port))

    self.db = self.connection['user_mgmt']
    self.collection = self.db['user_priority']
    self.connected = True


  def get_connection(self):
    if not self.connected:
      connect_db(self.config)

    return self.connection

  def get_db(self):
    if not self.connected:
      connect_db(self.config)

    return self.db

  def get_collection(self):
    if not self.connected:
      connect_db(self.config)

    return self.collection

  def get_users(self, query={}):
    if not self.connected:
      connect_db(self.config)

    project = {"_id":0} # hide ID
    return {doc['uid']:doc for doc in self.db['user_priority'].find(query, project)}

  def put_or_update_user(self, data):
    if data is None:
      return

    if not self.connected:
      connect_db(self.config)

    try:
      data['priority'] = int(data['priority'])
      #self.collection.insert_one(data)
      self.collection.update({'uid':data['uid']}, data, upsert=True)
    except Exception, e:
      print data, e
      return False
    return True

  def put_users(self, users=None):
    if users is None:
      return

    if not self.connected:
      connect_db(self.config)

    self.collection.insert(users)

  def delete_user(self, uid):
    if uid is None:
      return

    if not self.connected:
      connect_db(self.config)

    try:
      count = self.collection.delete_one({'uid':uid}).deleted_count
    except Exception, e:
      print data, e
      return False
    return True

  def delete_users(self, users=None):
    if users is None:
      return

    if not self.connected:
      connect_db(self.config)

    self.collection.insert(users)

   

   


