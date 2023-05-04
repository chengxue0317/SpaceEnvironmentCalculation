import dmPython
import sys
import numpy as np
from pathlib import Path
sys.path.append(str(Path(__file__).resolve().parents[1]))
import AP8AE8.ap8ae8 as ae
import BB0_LM_point.BB0LM as BB0LM
import random
import os
import re
import datetime
import shutil
from datetime import timezone
from matplotlib import pyplot as plt
#from mpl_toolkits.basemap import Basemap
from scipy import interpolate
from PIL import Image
import pandas as pd
from matplotlib import ticker, cm
from matplotlib import colors
import warnings
warnings.filterwarnings("ignore")

def Connect_SQL(iniPath):
    from configparser import ConfigParser
    cfg = ConfigParser()
    cfg.read(iniPath)
    sql_cfg = dict(cfg.items("dmsql"))
    conn = dmPython.connect(
        user=sql_cfg['user'],
        password=sql_cfg['password'],
        server=sql_cfg['server'],
        port=sql_cfg['port'])
    cursor = conn.cursor()
    return cursor,conn


# 更新for
def updateFile(file,old_str,new_str):
    with open(file, "r", encoding="utf-8") as f1,open("%s.bak" % file, "w", encoding="utf-8") as f2:
        for line in f1:
            f2.write(re.sub(old_str,new_str,line))
    os.remove(file)
    os.rename("%s.bak" % file, file)


# 读取flux文件
def Read_txt(dir_name_flux,filedir_flux):
  txt_all = []
  f = open(filedir_flux+'/'+dir_name_flux+'/flux.txt')
  file_size = len(f.readlines())
  ff = open(filedir_flux+'/'+dir_name_flux+'/flux.txt')
  for i in range(file_size):
    txt = list(map(float,ff.readline().replace('\n','').split(',')[3:5]))
    txt_all.append(txt)
  txt_all = np.array(txt_all)
  ind = np.where(txt_all<0)
  txt_all[ind] = 0
  return txt_all


# 更新F90文件
m = str(float(sys.argv[5])).replace('"','').replace("'",'')
n = str(float(sys.argv[6])).replace('"','').replace("'",'')
current_dir = os.path.dirname(os.path.abspath(__file__).replace('global_radiation',''))+'/AP8AE8'
shutil.copyfile(current_dir+'/AP8AE8_ini.f90',current_dir+'/AP8AE8.f90')
filename = current_dir+'/AP8AE8.f90'
find = 'E_start'
replace = m
updateFile(filename,find,replace)
find = 'E_end'
replace = n
updateFile(filename,find,replace)
if (float(m)>=float(n)) | (float(m)<0) | (float(n)<0):
    sys.exit()

# 编译Fortran程序
os.system('rm -rf '+current_dir+'/ap8ae8')
command = 'cd '+current_dir+'; gfortran AP8AE8.f90 libirbem.so -o ap8ae8'
os.system(command)

# 生成经纬网格
degree = float(sys.argv[4])
lon = np.arange(-180,181,degree)
lat = np.arange(90,-91,-degree)
lon2d,lat2d = np.meshgrid(lon,lat)

# 生成时间，本地时间
current_time = str(sys.argv[1])
current_time = current_time.replace('"','')
time_f107 = current_time.split()[0]

if len(current_time)==1:
    current_time = datetime.datetime.now()
    current_time = str(current_time).split('.')[0]
time_f107 = current_time.split()[0]


# 生成高度，单位：km
z = float(sys.argv[2])
if z> 200000:
    sys.exit()

lat1d = []
lon1d = []
time1d = []
z1d = []
for i in range(0,lon2d.shape[1]):
    for j in range(0,lon2d.shape[0]):
        lat1d.append(lat2d[j,i])
        lon1d.append(lon2d[j,i])
        time1d.append(current_time)
        z1d.append(z)

# 读取太阳F107数据
# 连接达梦数据库
iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
cursor,conn = Connect_SQL(iniPath)

Time_span = "select F107,TIME from SEC_F107_FLUX where TIME = '%s'" % (time_f107)  # time_f107[1:-1]
P = pd.read_sql(Time_span, conn)
F107 = P.F107.values
if len(F107)==0:
    sys.exit()
F107 = int(F107)
print('The F107 is %d'%(F107))


# 选择哪个通道的质子通量？
# 通道1为质子通道
# 通道2为电子通道
channel = int(sys.argv[3])

if (F107>180) | (F107<=90):
    if (channel==2) & (F107>180):
        max_min = 'ae8max'
    elif(channel==2) & (F107<=90):
        max_min = 'ae8min'
    elif(channel==1) & (F107>180):
        max_min = 'ap8max'
    elif(channel==1) & (F107<=90):
        max_min = 'ap8min'
   
    # 模拟高能粒子
    dir_name,filedir =ae.ap8ae8(time1d, z1d, lon1d, lat1d, whatf='above', whichm=max_min)
    f = Read_txt(dir_name,filedir)



elif (F107<=180) & (F107>90):
    if channel==2: # 电子
        dir_name_1,filedir_1 =ae.ap8ae8(time1d, z1d, lon1d, lat1d, whatf='above', whichm='ae8min')
        dir_name_2,filedir_2 =ae.ap8ae8(time1d, z1d, lon1d, lat1d, whatf='above', whichm='ae8max')
    elif channel==1: # 质子
        dir_name_1,filedir_1 =ae.ap8ae8(time1d, z1d, lon1d, lat1d, whatf='above', whichm='ap8min')
        dir_name_2,filedir_2 =ae.ap8ae8(time1d, z1d, lon1d, lat1d, whatf='above', whichm='ap8max')
    f1 = Read_txt(dir_name_1,filedir_1)
    f2 = Read_txt(dir_name_2,filedir_2)
    f = (f1+f2)/2

p = f[:,0]-f[:,1]
if sum(p)== 0.0:
    sys.exit()

s = 0
num_row = lon2d.shape[0]
num_column = lon2d.shape[1]
p2d = np.zeros([num_row,num_column])-88888
for i in range(0,num_column):
    m = i*num_row
    n = (i+1)*num_row
    p2d[:,s] = p[m:n]
    s = s+1
ind = np.where(p2d<0)
p2d[ind] = 0.001

if degree>1:
    # 将模拟结果插值成1度乘1度的分辨率
    lon = np.arange(-180,181,degree)
    lat = np.arange(90,-91,-degree)
    f = interpolate.interp2d(lon,lat,p2d,kind='cubic')
    lon = np.arange(-180,181,1)
    lat = np.arange(90,-91,-1)
    lon2d_1deg,lat2d_1deg = np.meshgrid(lon,lat)
    p2d_1deg = f(lon,lat)
    p2d_1deg[p2d_1deg<0]=0
else:
    p2d_1deg = p2d
    lat2d_1deg = lat2d
    lon2d_1deg = lon2d


# 画图
fig = plt.contourf(lon2d_1deg,lat2d_1deg,p2d_1deg,400,cmap='rainbow')
plt.rcParams["figure.facecolor"] = 'black'
plt.rcParams["savefig.facecolor"] = 'black'
#x轴刻度值trick的关闭
plt.xticks([])
#x轴刻度值trick的关闭
plt.yticks([])
#关闭上下左右坐标轴
plt.gca().spines['top'].set_visible(False)
plt.gca().spines['bottom'].set_visible(False)
plt.gca().spines['left'].set_visible(False)
plt.gca().spines['right'].set_visible(False)
# colorbar 设置
clb = plt.colorbar(orientation='horizontal',shrink=1.1)
clb.ax.set_title('[particles/(cm$^{2}$*s)]',color='white')
# 设置colorbar label的颜色
clb.ax.tick_params(labelcolor = 'white')
clb.ax.tick_params(color = 'white')

#from matplotlib.ticker import FormatStrFormatter
#import matplotlib.ticker as tick
#clb.ax.xaxis.set_major_formatter(tick.FormatStrFormatter('%.2g'))
if (F107>180) | (F107<=90):
    path = filedir+'/'+dir_name
else:
    path = filedir_1+'/'+dir_name_1

# 存colorbar图

plt.savefig(path+'/test.jpg',dpi=600)
img = Image.open(path+'/test.jpg')
# 左边界 #上边界  #右边界  #下边界
region = img.crop((100,2060,3840,2700))
region.save(path+'/colorbar.jpg')

# 存contourf图
plt.clf()
#m = Basemap(llcrnrlat=-90, llcrnrlon=-180, urcrnrlat=90, urcrnrlon=180)
#m.drawcoastlines()
plt.contourf(lon2d,lat2d,p2d,400,cmap='rainbow')
#x轴刻度值trick的关闭
plt.xticks([])
#x轴刻度值trick的关闭
plt.yticks([])
#关闭上下左右坐标轴
plt.gca().spines['top'].set_visible(False)
plt.gca().spines['bottom'].set_visible(False)
plt.gca().spines['left'].set_visible(False)
plt.gca().spines['right'].set_visible(False)

# 去除空白
plt.axis('off')
plt.gca().xaxis.set_major_locator(plt.NullLocator())
plt.gca().yaxis.set_major_locator(plt.NullLocator())
plt.subplots_adjust(top = 1, bottom = 0, right = 1, left = 0, hspace = 0, wspace = 0)
plt.margins(0,0)
plt.savefig(path+'/main_figure.jpg',dpi=600,pad_inches=0)

# 删除多余文件
if (F107>180) | (F107<=90):
    os.system('rm -rf '+filedir+'/'+dir_name+'/test.jpg')
    #os.system('rm -rf '+filedir+'/'+dir_name+'/flux.txt')
    os.system('rm -rf '+filedir+'/'+dir_name+'/libirbem.so')
    os.system('rm -rf '+filedir+'/'+dir_name+'/ap8ae8')
    #os.system('rm -rf '+filedir+'/'+dir_name+'/BB0LM.txt')
else:
    os.system('rm -rf '+filedir_1+'/'+dir_name_1+'/test.jpg')
    os.system('rm -rf '+filedir_1+'/'+dir_name_1+'/flux.txt')
    os.system('rm -rf '+filedir_1+'/'+dir_name_1+'/libirbem.so')
    os.system('rm -rf '+filedir_1+'/'+dir_name_1+'/ap8ae8')
    #os.system('rm -rf '+filedir_1+'/'+dir_name_1+'/BB0LM.txt')
    os.system('rm -rf -R '+filedir_2+'/'+dir_name_2)
  

# 输出文件路径
print('###'+path+'###')
