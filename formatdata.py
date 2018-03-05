# -*- coding: utf-8 -*-
"""
Created on Sat Mar  3 13:49:29 2018

@author: Mesh
"""

import os

os.chdir("..\data2")
os.remove("alldata.txt")

f = open("alldata.txt","a+")

for filename in os.listdir():
    if filename.endswith(".log"):
        fread = open(filename, "r")
        contents = fread.read()
        f.write(contents)
        #print(filename)
