# -*- coding: utf-8 -*-

from mod_python import apache, Session, util as modutil
import simplejson as json
import os
import sys

resurssit = "/nashome3/saelosmo/html/tiea218/teht5"
sys.path.append(resurssit)
import db_utils as dbutil


def validoi_data(data):
	"""Validoi get_datan paluuarvoa vastaavan dictionaryn kentät. Palauta uusi dict, jossa virheilmoitukset"""
	virheet = {}
	for key in data.keys():
		virheet[key] = ""
	if data["nimi"] == "":
		virheet["nimi"] = "Anna jokin nimi!"
	return virheet

def get_data(req):
	"""Palauta lomakkeen tiedot dictionaryna. Avaimet: nimi, kuvaus."""
	data = {}
	data["nimi"] = req.form.getfirst("nimi").strip()
	data["kuvaus"] = req.form.getfirst("kuvaus")
	return data

def data_ok(data):
	"""Palauta True, jos get_datan paluuarvoa vastaavan dictionaryn kentät ovat ok"""
	virheet = validoi_data(data)
	for key in virheet.keys():
		if virheet[key] != "":
			return False
	return True

def index(req):
	"""Palauta JSON-objekti, jossa annettuja kenttiä vastaavat virheet"""
	req.content_type = "text; charset=utf-8"

	data = get_data(req)
	virheet = validoi_data(data)
	return json.dumps(virheet)
