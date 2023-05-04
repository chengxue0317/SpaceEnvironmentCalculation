import numpy as np
from matplotlib import cm
import warnings
warnings.filterwarnings("ignore")
import pandas as pd
from matplotlib.colors import LinearSegmentedColormap
from matplotlib import pyplot as plt
from scipy.interpolate import interp1d
from PIL import Image
from pathlib import Path


def interpolate(inp, fi):
    i, f = int(fi // 1), fi % 1  # Split floating-point index into whole & fractional parts.
    j = i+1 if f > 0 else i  # Avoid index error.
    return round((1-f) * inp[i] + f * inp[j],2)
inp = np.arange(1,256).tolist()




x0 = np.array([3,4,67,5,34,24,4,56,32,57,100])
x_set = list(set(x0))
x_set_sort = np.array(sorted(x_set))
new_len = len(x_set_sort)

colormap_float = np.zeros( (255, 3), np.float )
for i in range(0, 255, 1):
       colormap_float[i, 0] = cm.jet(i)[0]
       colormap_float[i, 1] = cm.jet(i)[1]
       colormap_float[i, 2] = cm.jet(i)[2]

delta = (len(inp)-1) / (new_len-1)
outp = [interpolate(inp, i*delta) for i in range(new_len)]

f1 = interp1d(inp,colormap_float[:, 0])
f2 = interp1d(inp,colormap_float[:, 1])
f3 = interp1d(inp,colormap_float[:, 2])
color_r = f1(outp)
color_g = f2(outp)
color_b = f3(outp)
color_map = np.zeros((len(outp),3))
color_map[:,0] = color_r 
color_map[:,1] = color_g 
color_map[:,2] = color_b 

color_new = np.zeros( (len(x0), 3), np.float )
for i in range(len(x0)):
    ind = np.where(x_set_sort == x0[i])
    color_new[i,0] = color_r[ind]
    color_new[i,1] = color_g[ind]
    color_new[i,2] = color_b[ind]


rgb_table = LinearSegmentedColormap.from_list('sst cmap', color_map)
pur = np.arange(len(x_set_sort))
like = np.arange(len(x_set_sort))
plt.scatter(x=pur,y=like,c=x_set_sort,cmap=rgb_table)
plt.rcParams["figure.facecolor"] = 'black'
plt.rcParams["savefig.facecolor"] = 'black'
clb = plt.colorbar(orientation='horizontal',shrink=1.1)
clb.ax.tick_params(labelcolor = 'white')
clb.ax.tick_params(color = 'white')
clb.ax.tick_params(labelsize= 8)
clb.ax.set_title('[Air Density/(kg/m$^{3}$)]',color='white')

plt.savefig('test.jpg',dpi=600)
img = Image.open('test.jpg')
# 左边界 #上边界  #右边界  #下边界
region = img.crop((100,2060,3840,2700))
region.save('colorbar.jpg')


