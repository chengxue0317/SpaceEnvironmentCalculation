import shutil
import os
import re
import numpy as np


# 计算辐射剂量
def Cal_shieldose2(file_path, dir_name, Var, material, shield_type, sptrum_point, sptrum_E, sptrum_fx, sum_seconds):

    # 更新inp
    def updateFile(file, old_str, new_str):
        with open(file, "r", encoding="utf-8") as f1, open("%s.bak" % file, "w", encoding="utf-8") as f2:
            for line in f1:
                f2.write(re.sub(old_str, new_str, line))
        os.remove(file)
        os.rename("%s.bak" % file, file)

    # 创建模型运行的inp文件
    # if os.path.exists(path+filename) == False:
    shutil.copyfile(file_path+'/example_seu_ini.inp', file_path+'/' + dir_name + '/example_seu.inp')

    # 写入SHIELDOSE input文件
    filename = file_path+'/' + dir_name + '/example_seu.inp'
    find = 'aa'
    replace = str(material)+' 1 70 '+'2'
    updateFile(filename, find, replace)
    if Var == 'p':
        find = 'proton'
        replace = str(sptrum_point)
        updateFile(filename, find, replace)
        find = 'electron'
        replace = '0'
        updateFile(filename, find, replace)
    else:
        find = 'electron'
        replace = str(sptrum_point)
        updateFile(filename, find, replace)
        find = 'proton'
        replace = '0'
        updateFile(filename, find, replace)

    find = 'cc'
    replace = sptrum_E
    updateFile(filename, find, replace)
    find = 'dd'
    replace = sptrum_fx
    updateFile(filename, find, replace)
    find = 'ee'
    replace = str(sum_seconds)
    updateFile(filename, find, replace)
    # os.system('cp '+file_path+'/example_seu.inp '+file_path+'/'+dir_name)
    # print('cp '+file_path+'/example_seu.inp '+file_path+'/'+dir_name)

    # 运行FORTRAN seu.exe
    os.system('cd '+file_path+'/'+dir_name+';./seu.exe >> res.txt; mv *.OUT results.txt')

    # 读取生成的
    filename = file_path+'/'+dir_name+'/results.txt'
    file = open(filename)
    f_size = len(file.readlines())

    # target does data
    ind = np.zeros(3)
    s = 0
    file = open(filename)
    for i in range(0, f_size):
        f = file.readline()
        f = f.split()
        f = np.array(f)
        if len(f) != 0:
            if f[0] == 'Z(mils)':
                ind[s] = i+1
                s = s+1


# Shield_Type 1: DOSE AT TRANSMISSION SURFACE OF FINITE SLAB SHIELDS
# Shield_Type 2: DOES IN SEMI-INFINITE SLAB SHIELDS
# Shield_Type 3: 1/2 DOES AT CENTER OF SLAB SHIELDS
  
    z_mils = []
    z_mm = []
    z_g = []
    does = []
    file = open(filename)
    for i in range(0, f_size):
        f = file.readline()
        if shield_type == 1:    
            if (i > ind[0]) & (i < ind[0]+77) & (len(f) != 0):
                f = f.split()
                if (len(f) != 0):
                    z_mils.append(f[0])
                    z_mm.append(f[1])   
                    z_g.append(f[2])
                    does.append(f[-1])
        elif shield_type == 2:
            if (i > ind[1]) & (i < ind[1]+77) & (len(f) != 0):
                f = f.split()
                if (len(f) != 0):
                    z_mils.append(f[0])
                    z_mm.append(f[1])   
                    z_g.append(f[2])
                    does.append(f[-1])
        else:
            if (i > ind[2]) & (i < ind[2]+77) & (len(f) != 0):
                f = f.split()
                if (len(f) != 0):
                    z_mils.append(f[0])
                    z_mm.append(f[1])   
                    z_g.append(f[2])
                    does.append(f[-1])
    return does, z_mm