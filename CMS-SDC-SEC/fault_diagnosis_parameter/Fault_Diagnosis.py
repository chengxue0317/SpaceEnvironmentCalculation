import numpy
import dataset
import numpy as np
import plot_utils
from keras.models import Sequential
import os
from keras.layers import Dense
import tensorflow
from keras.optimizers import SGD
os.environ['TF_CPP_MIN_LOG_LEVEL']='2'
import dmPython
import configparser
import pandas as pd

def get_data_from_dmpython(cfg_file,tablename):
    config_2 = configparser.ConfigParser()
    # config_2.read("xw.ini",encoding="utf-8")
    config_2.read(cfg_file, encoding="utf-8")
    print(config_2)
    dmsql_cfg = dict(config_2.items('dmsql'))

    conn=dmPython.connect(**dmsql_cfg)
    print("成功连接数据库！")
    cursor = conn.cursor()


    # 从训练集中获取数据

    sql_train = "select F107,KP,AP,DST, R from (%s)"%(tablename)
    cursor.execute(sql_train)
    result_train = cursor.fetchall()
    # print(result)
    df_result_train = pd.DataFrame(list(result_train),columns=["F107","KP","AP","DST","R"])
    # print(df_result_train)

    df_X = df_result_train.loc[:,['F107','KP','AP','DST']]
    X = df_X.values


    df_Y = df_result_train.loc[:,['R']]
    Y = df_Y.values




    # # 从测试集中获取数据
    # sql_test = "select F107,KP,AP,DST, R from SEC_ML_Test"
    # cursor.execute(sql_test)
    # result_test = cursor.fetchall()
    # # print(result)
    # df_result = pd.DataFrame(list(result_test),columns=["F107","KP","AP","DST","R"])
    # # print(df_result)





    cursor.close()
    conn.close()
    return X,Y

def model_build(X, Y):
    from sklearn.model_selection import train_test_split
    # 划分训练集和测试集
    X_train, X_test, Y_train, Y_test = train_test_split(X, Y, test_size=0.3, random_state=10,shuffle=False)

    print("开始进行训练！")
    model = Sequential()
    # units表示当前神经元数量,激活函数类型sigmoid,input_dim表示输入数据特征维度1  # 堆叠到序列上
    # model.add(Dense(units=128, activation='ReLU', input_dim=4))
    model.add(Dense(units=12, activation='relu', input_dim=4))
    #
    model.add(Dense(units=1, activation='softmax'))  # 0.6067
    model.add(Dense(units=1, activation='softmax'))# 0.6067


    # 代价函数：均方误差  优化器：sgd 随机梯度下降算法   评估标准：accuracy 准确度
    model.compile(loss='categorical_crossentropy', optimizer='rmsprop', metrics=['accuracy'])


    # print(X,Y)
    # # epochs相当于回合， batch_size相当于每一轮每一次用几个样本。 比如100样本，那一次拿出10样本进行训练，一回合就要10次训练。 10*5000回合=50000次。
    model.fit(X_train,Y_train, validation_data=(X_test,Y_test),epochs=100, batch_size=1000)

    # model.predict(X_train)
    print("模型训练完成！")
    score, acc = model.evaluate(X_test, Y_test)

    print('Test score:', score)
    print('Test accuracy:', acc)

if __name__ == '__main__':

    # cfg_file = "xw.ini"
    # 切换成绝对路径，否则在cmd中会报错-configparser.NoSectionError。而在pycharm不会报错。
    cfg_file = r"E:\Code\Python\机器学习\专家系统实战\故障诊断多参数 _202302223\xw.ini"
    Table_name = 'SEC_Fault_Diag'

    X,Y = get_data_from_dmpython(cfg_file,Table_name)
    #
    # X_test, Y_test = get_data_from_dmpython(cfg_file, Table_name_Test)
    model_build(X,Y)

