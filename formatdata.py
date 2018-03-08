# -*- coding: utf-8 -*-
"""
Created on Sat Mar  3 13:49:29 2018

@author: Mesh
"""

import os
import pandas as pd

def gather_data_csv():
    os.chdir("/home/mesh/S10/Orga_Git/TensorFlow-on-Android-for-Human-Activity-Recognition-with-LSTMs/data")
    
    if(os.path.isfile("alldata.txt")):
        os.remove("alldata.txt")
    
    columns = ['user','activity','timestamp', 'x-acc', 'y-acc', 'z-acc', 'x-rot', 'y-rot', 'z-rot']
    last_stamp = 0
    adf = pd.DataFrame()
    
    for filename in os.listdir():
        if filename.endswith(".log"):
            df = pd.read_csv(filename, header = None, names = columns)
            df.drop('x-rot', axis=1, inplace=True)
            df.drop('y-rot', axis=1, inplace=True)
            df.drop('z-rot', axis=1, inplace=True)
            df['timestamp'] += last_stamp
            adf = pd.concat([adf, df], axis = 0)
            last_stamp = adf.tail(1)['timestamp'].item()

    os.chdir("..")
            
    return adf