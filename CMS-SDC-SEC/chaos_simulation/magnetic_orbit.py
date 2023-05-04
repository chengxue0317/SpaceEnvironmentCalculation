"""
传参：
python magnetic_orbit.py "FY2B" "2011-01-01 00:00:00" "2011-01-02 10:00:00"
python py文件 卫星类型 开始时间 结束时间
"""
import dmPython
import json
import sys
from chaosmagpy import load_CHAOS_matfile
from chaosmagpy.data_utils import mjd2000
import numpy as np
import glob
import datetime
import pandas as pd
import os
import warnings
warnings.filterwarnings('ignore')

pwd_z=os.getcwd()  #主项目路径
pwd_c=sys.path[0]  #我的项目路径
os.chdir(pwd_c)

FILEPATH_CHAOS = glob.glob('data/CHAOS-*.mat')[0]
R_REF = 6371.2  # 地球平均半径

def getB(time, calat, lon, radius):
    # give inputs
    theta = calat
    phi = lon
    radius = radius + R_REF  # radius from altitude in km
    time = time  # time in mjd2000

    # load the CHAOS model
    model = load_CHAOS_matfile(FILEPATH_CHAOS)
    # print(model)

    # print('Computing core field.')
    B_core = model.synth_values_tdep(time, radius, theta, phi)  # 返回随时间变化的径向、余纬、垂直方向地磁分量

    # print('Computing crustal field up to degree 110.')
    B_crust = model.synth_values_static(radius, theta, phi, nmax=110)  # 返回静态内磁场径向、余纬、垂直方向地磁场

    # complete internal contribution
    B_radius_int = B_core[0] + B_crust[0]
    B_theta_int = B_core[1] + B_crust[1]
    B_phi_int = B_core[2] + B_crust[2]

    # print('Computing field due to external sources, incl. induced field: GSM.')
    B_gsm = model.synth_values_gsm(time, radius, theta, phi, source='all')  # 返回远磁层磁场径向、余纬、垂直方向地磁场

    # print('Computing field due to external sources, incl. induced field: SM.')
    B_sm = model.synth_values_sm(time, radius, theta, phi, source='all')  # 返回近磁层磁场径向、余纬、垂直方向地磁场

    # complete external field contribution
    B_radius_ext = B_gsm[0] + B_sm[0]
    B_theta_ext = B_gsm[1] + B_sm[1]
    B_phi_ext = B_gsm[2] + B_sm[2]

    # complete forward computation
    B_radius = B_radius_int + B_radius_ext
    B_theta = B_theta_int + B_theta_ext
    B_phi = B_phi_int + B_phi_ext

    B = np.sqrt(B_radius ** 2 + B_theta ** 2 + B_phi ** 2)
    # B = B.reshape(len(calat), len(lon))
    BX = np.negative(B_theta)
    BY = B_phi
    BZ = np.negative(B_radius)
    return B,BX,BY,BZ


def out_date(year, day):
    fir_day = datetime.datetime(year, 1, 1)
    zone = datetime.timedelta(days=day - 1)
    return datetime.datetime.strftime(fir_day + zone, "%Y-%m-%d")
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
    cur = conn.cursor()
    # if cur:
    #     print('>>>dmsql数据库连接成功<<<')
    return conn, cur

def main(sat_logo,dt_s,dt_e,iniPath, batch_size=100000):
    # try:
        conn, cur = Connect_SQL(iniPath)
        sql_select = "SELECT TIME,LAT,LON,ALT FROM SEC_SATELLITE_LLA" + " WHERE TIME between '%s' and '%s' and SAT_ID ='%s' " % (dt_s, dt_e,sat_logo)
        data = pd.read_sql(sql_select, conn)
        sql = "SELECT KP FROM (SELECT f.*, CASE " \
                     "when TIME < concat(substring(Cast(TIME As Varchar),0,10), ' 1:15:00') then (concat( substring( Cast(DATEADD(dd,-1,TIME) As Varchar), 0, 10 ), ' 22:15:00' )) " \
                     "when concat(substring(Cast(TIME As Varchar), 0,10),' 1:15:00') <= TIME AND TIME < concat(substring(Cast(TIME As Varchar),0,10),' 4:15:00') then (concat( substring( Cast(TIME As Varchar), 0, 10 ), ' 1:15:00' ))" \
                     "when concat(substring(Cast(TIME As Varchar), 0,10),' 4:15:00') <= TIME AND TIME < concat(substring(Cast(TIME As Varchar),0,10),' 7:15:00') then (concat( substring( Cast(TIME As Varchar), 0, 10 ), ' 4:15:00' ))" \
                     "when concat(substring(Cast(TIME As Varchar), 0,10),' 7:15:00') <= TIME AND TIME < concat(substring(Cast(TIME As Varchar),0,10),' 10:15:00') then (concat( substring( Cast(TIME As Varchar), 0, 10 ), ' 7:15:00' ))" \
                     "when concat(substring(Cast(TIME As Varchar), 0,10),' 10:15:00') <= TIME AND TIME < concat(substring(Cast(TIME As Varchar),0,10),' 13:15:00') then (concat( substring( Cast(TIME As Varchar), 0, 10 ), ' 10:15:00' ))" \
                     "when concat(substring(Cast(TIME As Varchar), 0,10),' 13:15:00') <= TIME AND TIME < concat(substring(Cast(TIME As Varchar),0,10),' 16:15:00') then (concat( substring( Cast(TIME As Varchar), 0, 10 ), ' 13:15:00' ))" \
                     "when concat(substring(Cast(TIME As Varchar), 0,10),' 16:15:00') <= TIME AND TIME < concat(substring(Cast(TIME As Varchar),0,10),' 19:15:00') then (concat( substring( Cast(TIME As Varchar), 0, 10 ), ' 16:15:00' ))" \
                     "when concat(substring(Cast(TIME As Varchar), 0,10),' 19:15:00') <= TIME AND TIME < concat(substring(Cast(TIME As Varchar),0,10),' 22:15:00') then (concat( substring( Cast(TIME As Varchar), 0, 10 ), ' 19:15:00' ))" \
                     "when concat(substring(Cast(TIME As Varchar), 0,10),' 22:15:00') <= TIME AND TIME < concat(substring(Cast(TIME As Varchar),0,10),' 23:59:59') then (concat( substring( Cast(TIME As Varchar), 0, 10 ), ' 22:15:00' ))" \
                     "ELSE 1 END  AS ND " \
                     "from SDC.SEC_SATELLITE_LLA f where TIME between '%s' and '%s' and SAT_ID='%s') MM Left JOIN SDC.SEC_KP_INDEX INDE ON  MM.ND = INDE.TIME" % (dt_s,dt_e,sat_logo)
        KP = pd.read_sql(sql, conn)
        if (data.TIME[1] - data.TIME[0]).seconds < 60:
            a=[]
            for i in range(0,len(data),60//(data.TIME[1] - data.TIME[0]).seconds):
                a.append(i)
            data=data.iloc[a]
            KP=KP.iloc[a]
        TIME, ALT, LAT, LON, B,B_X,B_Y,B_Z, Lm, BB0 = [], [], [], [], [], [], [],[],[],[]
        for i in range(0, len(data), batch_size):
            dt=data.iloc[i:i + batch_size, 0].values
            lat = data.iloc[i:i + batch_size,1].values
            lon = data.iloc[i:i + batch_size, 2].values
            alt = data.iloc[i:i + batch_size, 3].values
            kp = KP.iloc[i:i + batch_size, 0].values
            # dts = np.asarray(dt, dtype=np.unicode_)
            # time = (dts.astype('datetime64[us]') - np.datetime64('2000-01-01', 'us')) / np.timedelta64(1, 'D')
            from dateutil.relativedelta import relativedelta
            for i, t in enumerate(dt):
                # t = datetime.datetime.strptime(t, "%Y-%m-%d %H:%M:%S")
                if pd.to_datetime(t) > datetime.datetime.strptime('2022-08-31 00:00:00', "%Y-%m-%d %H:%M:%S"):
                    t = pd.to_datetime(t) - relativedelta(years=20)
                    dt[i]=t
            time = mjd2000(dt)
            calat=90 - lat
            #get B
            b,bx,by,bz = getB(time, calat, lon, alt)


            # get LM BBO
            from IRBEM import MagFields
            # model = MagFields(options=[0, 0, 0, 0, 0], verbose=True)
            model = MagFields(options=[0, 0, 0, 0, 0], verbose=True)

            LLA = {}
            LLA['dateTime'] = pd.to_datetime(dt).strftime('%Y-%m-%d %H:%M:%S').tolist()
            LLA['x2'] = lat
            LLA['x3'] = lon
            LLA['x1'] = alt
            maginput = {'Kp': kp}

            model.make_lstar(LLA, maginput)
            lm = model.make_lstar_output['Lm']
            blocal = model.make_lstar_output['blocal']
            bmin = model.make_lstar_output['bmin']
            bb0=np.array(blocal)/np.array(bmin)


            #TIME.extend(LLA['dateTime'])
            #ALT.extend(list(alt))
            #LAT.extend(list(lat))
            #LON.extend(list(lon))
            #B.extend(list(np.squeeze(b)))
            #B_X.extend(list(np.squeeze(bx)))
            #B_Y.extend(list(np.squeeze(by)))
            #B_Z.extend(list(np.squeeze(bz)))
            #Lm.extend(list(lm))
            #BB0.extend(list(np.squeeze(bb0)))

            TIME.extend(LLA['dateTime'])
            ALT.extend(list(np.round(alt,3)))
            LAT.extend(list(np.round(lat,3)))
            LON.extend(list(np.round(lon,3)))
            B.extend(list(np.round(np.squeeze(b),3)))
            B_X.extend(list(np.round(np.squeeze(bx),3)))
            B_Y.extend(list(np.round(np.squeeze(by),3)))
            B_Z.extend(list(np.round(np.squeeze(bz),3)))
            Lm.extend(list(np.round(lm,3)))
            BB0.extend(list(np.round(np.squeeze(bb0),3)))

        # save to output file
        DATA = np.stack([np.array(TIME), np.array(ALT), np.array(LAT), np.array(LON), np.array(B),np.array(B_X),np.array(B_Y),np.array(B_Z),np.array(Lm),np.array(BB0)], axis=-1)
        DATA=pd.DataFrame(DATA)
        DATA.columns=['time','alt(km)','lon(deg)','lat(deg)','B(nT)','B_X(nT)','B_Y(nT)','B_Z(nT)','BB0','Lm']
        # header = ('t(mjd2000) alt(km) calat(deg) lon(deg) '
        #           'B(nT) B0(nT) BB0')
        # np.savetxt('./out/B_orbit.txt', data, fmt="%f", delimiter=' ', header=header)
        # print(DATA)
        os.chdir(pwd_z)
        DATA=DATA.to_json(orient ='records',force_ascii=False)#orient="index",
        return DATA

    # except:
    #     return False
if __name__ == '__main__':

    k=sys.argv[1:]
    a=main(k[0],k[1][1:-1],k[2][1:-1],k[3])
    print('%%',a,'%%')

    # k=sys.argv[1:]
    # a=main(k[0],k[1],k[2],k[3])
    # print('%%',a,'%%')


    # sat_logo='FY2B'
    # dt_s='2011-11-08 00:00:00'
    # dt_e='2011-11-08 01:00:00'
    # iniPath = 'xw.ini'
    # a =main(sat_logo,dt_s,dt_e,iniPath)
    # print(a)
