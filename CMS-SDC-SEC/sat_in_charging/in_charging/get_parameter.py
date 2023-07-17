import sys
import os
para_name = []
current_dir = os.path.dirname(os.path.abspath(__file__))

f = open(current_dir+'/parameter.txt')
size = len(f.readlines())
f = open(current_dir+'/parameter.txt')
for i in range(size):
	txt = f.readline().split()
	if i>0:
		para_name.append(txt[0])
		#para_conductivity.append(txt[1])
		#para_density.append(txt[2])

# 以JSON形式显示结果
dic = {}
dic['parameter_name'] = para_name
print(dic)
