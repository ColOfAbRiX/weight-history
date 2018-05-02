#!/usr/bin/env python

import csv
import json
import requests
import argparse

parser = argparse.ArgumentParser()
parser.add_argument(
    'endpoint',
    help='The HTTP endpoint for the REST API.'
)
parser.add_argument(
    'input_csv',
    help='The path of the input CSV.'
)
args = parser.parse_args()

with open(args.input_csv, 'r') as file:
    headers = {'Content-type': 'application/json'}
    for row in csv.reader(file):
        data = {
            "date": row[0],
            "weight": float(row[1]),
            "fat": float(row[2]),
            "water": float(row[3]),
            "muscle": float(row[4]),
        }
        data = json.dumps(data)
        print("Adding: %s" % data)
        response = requests.put(args.endpoint, data=data, headers=headers)
        if response.status_code != 200:
            print("UNEXPECTED ANSWER")
            print(response)
            input("Press ENTER to continue to the next entry...")
