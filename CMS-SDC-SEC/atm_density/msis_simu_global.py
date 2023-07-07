import dmPython
import pandas as pd
import warnings
warnings.filterwarnings("ignore")
import os
import numpy as np
np.set_printoptions(suppress=True)
import datetime
import random
import sys
import re
import time
from matplotlib import ticker, cm
from matplotlib import colors
from matplotlib import pyplot as plt
from PIL import Image

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

def replace_char(old_string, char, index):
    """
    字符串按索引位置替换字符
    old_string: 原始字符串
    char： 要替换成啥？
    index： 下标
    """
    old_string = str(old_string)
    # 新的字符串 = 旧字符串[:要替换的索引位置] + 替换成的目标字符 + 旧字符串[要替换的索引位置+1:]
    new_string = old_string[:index] + char + old_string[index + 1:]
    return new_string

def timestamp(shijian):
    s_t=time.strptime(shijian,"%Y-%m-%d %H:%M:%S")
    mkt=int(time.mktime(s_t))
    return(mkt)

def shijian(timeStamp):
    timeArray = time.localtime(timeStamp)
    otherStyleTime = time.strftime("%Y-%m-%d %H:%M:%S", timeArray)
    return otherStyleTime

# 读取flux文件
def Read_txt(dir_name_flux,filedir_flux):
  txt_all = []
  f = open(filedir_flux+'/figure/'+dir_name_flux+'/msis2.0_simulations.txt')
  file_size = len(f.readlines())
  ff = open(filedir_flux+'/figure/'+dir_name_flux+'/msis2.0_simulations.txt')
  for i in range(file_size):
    #print(ff.readline().replace(' ','').replace('\n',''))
    txt = float(ff.readline().replace(' ','').replace('\n',''))
    txt_all.append(txt)
  txt_all = np.array(txt_all)
  return txt_all

# 生成经纬网格
degree = 1.0
lon = np.arange(-180,181,degree)
lat = np.arange(90,-91,-degree)
lon2d,lat2d = np.meshgrid(lon,lat)

# 生成时间，F107时间
current_time = str(sys.argv[1])
current_time = current_time.replace('"','')
if (len(current_time.split('-')[1])==1):
    current_time = current_time[:5]+'0'+current_time[5:]
if (len(current_time.split('-')[2].split(' ')[0])==1):
    current_time = current_time[:8]+'0'+current_time[8:]
time_f107 = current_time.split()[0]

if len(current_time)==1:
    current_time = datetime.datetime.now()
    current_time = str(current_time).split('.')[0]
time_f107 = current_time.split()[0]
time_str = time_f107+ ' 12:00:00'
# F107当前时间的前一天时间：previous day of F107 index
time_f107_previous_day = str(shijian(timestamp(time_str)-86400)).split(' ')[0]
# F107 81天区间时间
time_f107_81_start = str(shijian(timestamp(time_str)-86400*40)).split(' ')[0]
time_f107_81_end = str(shijian(timestamp(time_str)+86400*40)).split(' ')[0]


# 生成高度，单位：km
z = float(sys.argv[2])
if z> 2000:
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


# 连接达梦数据库
iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
cursor,conn = Connect_SQL(iniPath)

# 读取太阳F107数据
Time_span = "select F107,TIME from SEC_F107_FLUX where TIME = '%s'" % (time_f107_previous_day)
P = pd.read_sql(Time_span, conn)
F107 = P.F107.values
if len(F107)==0:
    sys.exit()
F107_previous_day = int(F107)
Time_span = "select F107,TIME from SEC_F107_FLUX where TIME between '%s' and '%s'" % (time_f107_81_start,time_f107_81_end)
P = pd.read_sql(Time_span, conn)
F107 = P.F107.values
if len(F107)==0:
    sys.exit()
F107_81mean = round(np.nanmean(F107))
print('The previous day of F107 is %d'%(F107_previous_day))
print('The 81mean F107 is %d'%(F107_81mean))

# 读取AP数据
t_std = np.array([1,4,7,10,13,16,19,22])
ap_time = current_time.split(':')[0]+':15:00'
print(ap_time)
t = int(ap_time[11:13])
ind = np.where(abs(t_std-t)== min(abs(t_std-t)))
k = str(t_std[ind][0])
if len(k)<2:
    k = '0'+str(t_std[ind][0])
ap_time_end = replace_char(ap_time,k[0],11)
ap_time_end = replace_char(ap_time_end,k[1],12)

ap_time_start = shijian(timestamp(ap_time_end)-57*3600)
ap_time_end = ap_time_end.replace(':00',':01')
Time_span = "select AP,TIME from SEC_AP_INDEX where TIME between '%s' and '%s'" % (ap_time_start,ap_time_end)
P = pd.read_sql(Time_span, conn)
#print(P.TIME.values)
AP = P.AP.values.astype(np.int)
AP1 = AP[19]
AP2 = AP[19]
AP3 = AP[18]
AP4 = AP[17]
AP5 = AP[16]
AP6 = round(np.nanmean(AP[8:16]))
AP7 = round(np.nanmean(AP[0:8]))

# day of year
day = datetime.datetime.strptime(ap_time_end, '%Y-%m-%d %H:%M:%S')
day_of_year = day.strftime("%j")

# UTC
utc = int(current_time[11:13])*3600 + int(current_time[14:16])*60 + int(current_time[17:19])

# 判断是否存在figure文件夹
current_dir = os.path.dirname(os.path.abspath(__file__))
if os.path.exists(current_dir+'/figure') == False:
    os.system('mkdir '+current_dir+'/figure')

# 建立模拟结果文件夹(唯一性)
current_dir = current_dir
while True:
    dir_name = str(random.randint(1,99999))
    if os.path.exists(current_dir+'/figure/'+dir_name) == False:
        os.system('mkdir '+current_dir+'/figure/'+dir_name)
        break
os.system('cp '+current_dir+'/'+ 'msis2.0_test_storm.exe '+current_dir+'/figure/'+dir_name)
os.system('cp '+current_dir+'/'+ 'msis20.parm '+current_dir+'/figure/'+dir_name)


# 将msis背景场写入Txt文件
with open(current_dir+'/figure/'+dir_name+'/msis2.0_forcing.txt','w',encoding='utf-8') as f:
    for i in range(0, len(lat1d)):
        f.write(str(day_of_year))  
        f.write('  ')
        f.write(str(utc))
        f.write('  ')
        f.write(str(float(z1d[i])))
        f.write('  ')
        f.write(str(float(lat1d[i])))
        f.write('  ')
        f.write(str(float(lon1d[i])))
        f.write('  ')
        f.write('99')
        f.write('  ')
        f.write(str(F107_81mean))
        f.write('  ')
        f.write(str(F107_previous_day))
        f.write('  ')
        f.write(str(AP1))
        f.write('  ')
        f.write(str(AP2))
        f.write('  ')
        f.write(str(AP3))
        f.write('  ')
        f.write(str(AP4))
        f.write('  ')
        f.write(str(AP5))
        f.write('  ')
        f.write(str(AP6))
        f.write('  ')
        f.write(str(AP7))
        f.write('\n')

# 运行FORTRAN seu.exe
os.system('cd '+current_dir+'/figure/'+dir_name+';  ./msis2.0_test_storm.exe >/dev/null 2>&1') 
txt = Read_txt(dir_name,current_dir)

# 1d 转化为2d
s = 0
num_row = lon2d.shape[0]
num_column = lon2d.shape[1]
p2d = np.zeros([num_row,num_column])-88888
for i in range(0,num_column):
    m = i*num_row
    n = (i+1)*num_row
    p2d[:,s] = txt[m:n]
    s = s+1
p2d = p2d*1000

#######################################################################
# 画图
fig = plt.contourf(lon2d,lat2d,p2d,400,cmap='rainbow')
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
clb.ax.set_title('[Air Density/(kg/m$^{3}$)]',color='white')
# 设置colorbar label的颜色
clb.ax.tick_params(labelcolor = 'white')
clb.ax.tick_params(color = 'white')
clb.ax.tick_params(labelsize= 8)

#from matplotlib.ticker import FormatStrFormatter
#import matplotlib.ticker as tick
#clb.ax.xaxis.set_major_formatter(tick.FormatStrFormatter('%.2g'))
path = current_dir+'/figure/'+dir_name


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

# 删除多余数据
os.system('rm -rf '+current_dir+'/figure/'+dir_name+'/test.jpg')
os.system('rm -rf '+current_dir+'/figure/'+dir_name+'/msis2.0_simulations.txt')
os.system('rm -rf '+current_dir+'/figure/'+dir_name+'/msis2.0_forcing.txt')
os.system('rm -rf '+current_dir+'/figure/'+dir_name+'/msis2.0_test_storm.exe')
os.system('rm -rf '+current_dir+'/figure/'+dir_name+'/msis20.parm')

# 输出文件路径
print('###'+path+'###')