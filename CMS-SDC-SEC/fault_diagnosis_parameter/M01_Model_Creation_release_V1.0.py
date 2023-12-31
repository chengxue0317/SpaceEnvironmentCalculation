# import json
import sys

import numpy as np
# import numpy
# import dataset
# import numpy as np
# import plot_utils
from keras.models import Sequential
import os
from keras.layers import Dense
# import train
import matplotlib.pyplot as plt
# import tensorflow
from keras.optimizers import SGD
os.environ['TF_CPP_MIN_LOG_LEVEL']='2'
import dmPython
import configparser
import pandas as pd
# from matplotlib.figure import Figure
import joblib as jl
import shutil
import time

# 旧版本调用数据库代码
# def get_data_from_dmpython(cfg_file,tablename):
#     # print(cfg_file,tablename)
#     config_2 = configparser.ConfigParser()
#     # config_2.read("xw.ini",encoding="utf-8")
#     config_2.read(cfg_file, encoding="utf-8")
#     # print(config_2)
#     dmsql_cfg = dict(config_2.items('dmsql'))
#
#     conn=dmPython.connect(**dmsql_cfg)
#     print("成功连接数据库！")
#     cursor = conn.cursor()
#
#
#     # 从训练集中获取数据
#
#     sql_train = "select F107,KP,AP,DST, R from (%s)"%(tablename)
#     cursor.execute(sql_train)
#     result_train = cursor.fetchall()
#     # print(result)
#     df_result = pd.DataFrame(list(result_train),columns=["F107","KP","AP","DST","R"])
#     # print(df_result_train)
#
#     df_X = df_result.loc[:,['F107','KP','AP','DST']]
#     X = df_X.values
#
#
#     df_Y = df_result.loc[:,['R']]
#     Y = df_Y.values
#
#     cursor.close()
#     conn.close()
#     return X,Y



# 新版本数据库代码
def get_data_from_dmpython(cfg_file,tablename,Characters_X,Characters_Y):
    # print(cfg_file,tablename)
    config_2 = configparser.ConfigParser()
    # config_2.read("xw.ini",encoding="utf-8")
    config_2.read(cfg_file, encoding="utf-8")
    # print(config_2)
    dmsql_cfg = dict(config_2.items('dmsql'))

    conn=dmPython.connect(**dmsql_cfg)
    print("成功连接数据库！")
    cursor = conn.cursor()


    # 从训练集中获取数据

    sql_train_X = "Select %s from (%s)"%(Characters_X,tablename)

    cursor.execute(sql_train_X)
    result_X = cursor.fetchall()
    # print(result_X)
    # print(result)
    df_X = pd.DataFrame(list(result_X))
    # 补充空值填充0
    df_X = df_X.fillna(0)
    # print(df_result)
    # colnum = len(df_result.columns)
    #
    # df_X = pd.concat([df_result.iloc[:,2],df_result.iloc[:,6:]],join="outer",axis=1)
    X = df_X.values


    sql_train_Y = "select (%s) from (%s)"%(Characters_Y,tablename)
    cursor.execute(sql_train_Y)
    result_Y = cursor.fetchall()

    # print(result)
    df_Y = pd.DataFrame(list(result_Y))

    # 补充填充值0
    df_Y = df_Y.fillna(0)


    Y = np.ravel(df_Y.values)

    # print(Y)

    cursor.close()
    conn.close()
    return X,Y





def loss_and_accuracy(history_dict,imgpath):


    loss = list(history_dict)[0]
    accuracy = list(history_dict)[1]
    val_loss = list(history_dict)[2]
    val_accuracy = list(history_dict)[3]

    loss_values = history_dict[loss]
    val_loss_values = history_dict[val_loss]
    epochs = range(1,len(loss_values) + 1)
    plt.plot(epochs,loss_values,'bo',label = 'Training loss')
    plt.plot(epochs,val_loss_values,'b',label = 'Validation loss')



    title = 'Training and validation loss'
    plt.title('Training and validation loss')
    plt.xlabel('Epochs')
    plt.ylabel('Loss')
    plt.legend()
    # plt.show()

    plt.savefig('./' + imgpath + '/{}.png'.format(title))

    plt.clf()
    acc_values = history_dict[accuracy]
    val_acc_values = history_dict[val_accuracy]

    plt.plot(epochs, acc_values, 'bo', label='Training acc')
    plt.plot(epochs, val_acc_values, 'b', label='Validation acc')
    title = 'Training and validation accuracy'
    plt.title(title)
    plt.xlabel('Epochs')
    plt.ylabel('Acc')
    plt.legend()
    # plt.show()
    plt.savefig('./' + imgpath + '/{}.png'.format(title))
    # plt.savefig('./img/{}.png',title)

def model_build(X, Y,cfg_file):

    number_of_character = X.shape[1]
    # print(number_of_character)
    # 获取最大值，决定softmax输出像元大小
    number_of_class = max(set(Y.tolist())) + 1

    config = configparser.ConfigParser()
    # config_1.read("info.ini", encoding="utf-8")
    config.read(cfg_file,encoding="utf-8")

    # 输入层参数获取
    num_of_dense_in_input_layer  = int(config.get("parameter", 'num_of_dense_in_input_layer'))
    input_dim = number_of_character
    activation_input = config.get("parameter", 'activation_input')

    # 隐藏层参数获取
    num_of_hidden_layer = int(config.get("parameter", 'num_of_hidden_layer'))
    num_of_dense_in_hidden_layer = int(config.get("parameter", 'num_of_dense_in_hidden_layer'))
    activation_hidden = config.get("parameter", 'activation_hidden')

    # 输出层参数获取
    # num_of_dense_in_output_layer = int(config.get("parameter", 'num_of_dense_in_output_layer'))
    num_of_dense_in_output_layer = number_of_class
    activation_output = config.get("parameter", 'activation_output')
    # print(activation_output)

    #
    loss = config.get("parameter", 'loss')
    # print(loss)


    optimizer = config.get("parameter",'optimizer')

    learning_rate = config.get("parameter", 'learning_rate')

    decay = config.get("parameter", 'decay')

    metrics=config.get("parameter",'metrics')

    # 参数设置 生成多少条数据
    epochs = int(config.get("parameter",'epochs'))
    batch_size = int(config.get("parameter",'batch_size'))

    verbose = int(config.get("parameter", 'verbose'))


    from sklearn.model_selection import train_test_split
    # 划分训练集和测试集
    X_train, X_test, Y_train, Y_test = train_test_split(X, Y, test_size=0.3, random_state=10,shuffle=False)
    # print(X_train.shape,Y_train.shape)

    model = Sequential()
    # 输入层
    # print(activation_input)
    model.add(Dense(units=num_of_dense_in_input_layer,input_dim=input_dim,activation=activation_input))
    for i in range(num_of_hidden_layer):
        model.add(Dense(num_of_dense_in_hidden_layer, activation=activation_hidden))
        # model.add(Dense(64, activation="relu"))
    # 输出层
    model.add(Dense(num_of_dense_in_output_layer, activation=activation_output))


    # 代价函数：均方误差  优化器：sgd 随机梯度下降算法   评估标准：accuracy 准确度
    from keras.optimizers import Adam
    from keras import optimizers
    # adam = optimizers.Adam(learning_rate=0.8, beta_1=0.9, beta_2=0.999, epsilon=None, decay=0.0, amsgrad=False)

    # model.compile(loss='mean_squared_error', optimizer=SGD(learning_rate=0.1),metrics='accuracy')
    from keras import losses
    # binary CrossEntorpy 二分类
    # CrossEntropy可用于多分类任务，且label且one-hot形式。它的计算式如下：
    # 跟categorical_crossentropy的区别是其标签不是one-hot，而是integer。比如在categorical_crossentropy是[1,0,0]，在sparse_categorical_crossentropy中是3.
    #
    # 传参的方式调用交叉熵方法。
    # optimizer='RMSprop'
    # lr=1e-3
    # metrics = 'accuracy'
    # decay = 0.0


    str = "model.compile(loss=losses.{0}, optimizer=optimizers.{1}(learning_rate={2},decay={3}),metrics='{4}')".format(loss,optimizer,learning_rate,decay,metrics)

    # print(str)
    eval(str)




    print("深度神经网络模型开始进行训练！")
    #  fit( ) 中 的 verbose 参数：
    # verbose：日志显示
    # verbose = 0 为不在标准输出流输出日志信息
    # verbose = 1 为输出进度条记录
    # verbose = 2 为每个epoch输出一行记录
    # 注意： 默认为 1
    model.fit(X_train,Y_train, validation_data=(X_test,Y_test),epochs=epochs,batch_size=batch_size,verbose=verbose)

    # 生成图的代码，非必要
    # history_dict = hist.history
    # loss_and_accuracy(history_dict,imgpath)


    print("深度神经网络模型训练完成！")

    score, acc = model.evaluate(X_test, Y_test,verbose=2)



    print('Test score:', score)
    print('Test accuracy:', acc)


    return model

def model_copy( modelpath,model_fullpath,model_name,time_tag ):
    if os.path.exists(model_fullpath):
        # t = time.localtime()
        # timetag = str(t.tm_year) + str(t.tm_mon).zfill(2) + str(t.tm_mday).zfill(2) + str(t.tm_hour).zfill(2) + str(t.tm_min).zfill(2) + str(t.tm_sec).zfill(2)
        shutil.copyfile(model_fullpath,modelpath+'/{}_'.format(model_name)+time_tag+'.pkl')

def model_save(model,model_path,model_name,Characters_X,time_tag):
    Characters_name =Characters_X.replace(",","_")
    # print(model.input_shape[1])
    # path = ('./' + modelpath + '/{}.pkl'.format(model_name))
    # modelpath = ('./' + 'model')
    # if not os.path.exists(modelpath):
    #     os.mkdir(modelpath)
    # file_exists('model')

    model_fullpath = model_path + '/{}'.format(model_name)+'_'+ Characters_name+'_'+time_tag+'.pkl'

    # 如果训练成功，则按照时间戳事先保存原先版本；
    # model_copy(modelpath, model_fullpath, model_name,time_tag)


    # 对新模型进行保存
    jl.dump(model, model_fullpath)
    #
    print("新模型保存成功")
    return model_fullpath
#     #######################################################################################

def time_tag_creation():
    t = time.localtime()
    time_tag = str(t.tm_year) + str(t.tm_mon).zfill(2) + str(t.tm_mday).zfill(2) + str(t.tm_hour).zfill(2) + str(
        t.tm_min).zfill(2) + str(t.tm_sec).zfill(2)
    return time_tag

def file_exists(str_path):
    if not os.path.exists(str_path):
        os.mkdir(str_path)

if __name__ == '__main__':
    # cfg_file = "xw.ini"
    # 切换成绝对路径，否则在cmd中会报错-configparser.NoSectionError。而在pycharm不会报错。
    # 创建时间戳
    # time_tag = time_tag_creation()
    # 接收传递过来的log文件名 如 20230302145933
    # time_tag = str(20230302145933)
    time_tag = sys.argv[1]
    # print(time_tag)

    # 创建图片文件夹
    # imgpath = 'img'
    # # print('./' + imgpath + '/{}.png'.format('abc'))
    # if not os.path.exists(imgpath):
    #     os.mkdir(imgpath)

    # python "E:\Code\Python\机器学习\专家系统实战\故障诊断多参数_202302223\M01_Model_Creation_release_V1.1.py" "20230302145933" "E:\Code\Python\机器学习\专家系统实战\故障诊断多参数_202302223\xw.ini" "SEC_Fault_DIAGNOSIS_COPY" "Confidence,F107,KP" "Error_ID"

    # 传递配置文件
    # cfg_file = r"E:\Code\Python\机器学习\专家系统实战\故障诊断多参数_202302223\xw.ini"
    cfg_file = sys.argv[2]

    # 传递数据库表名
    # Table_name = 'SEC_Fault_DIAGNOSIS_COPY'
    Table_name = sys.argv[3]

    # 传递数据库表名
    Characters_X = sys.argv[4]
    # Characters_X = 'Confidence,F107,KP'

    # list_Characters_X = Characters_X.split()
    # Characters_Y = 'Error_ID'
    Characters_Y = sys.argv[5]

    # 创建log文件夹
    log_path = os.path.dirname(cfg_file)+"//"+"log"
    file_exists(log_path)
    f = open(log_path+'/{}.log'.format(time_tag), 'w', encoding="utf-8")
    # 开始写入日志文件
    temp = sys.stderr
    
    sys.stdout = f



    X, Y = get_data_from_dmpython(cfg_file, Table_name, Characters_X,Characters_Y)

    # print(X)
    # print(Y)

    # print(X.shape)
    # 模型构建，模型名字的前半部分默认值
    default_model_name = "Fault_Diagnose"


    model = model_build(X, Y,cfg_file)
    input_shape = model.input_shape[1]

    model_path = os.path.dirname(cfg_file) + "//" + "model" + "//" + str(input_shape) + "特征量模型"
    file_exists(model_path)


    # 模型保存，返回模型全路径
    model_fullpath = model_save(model,model_path,default_model_name,Characters_X,time_tag=time_tag)
    #
    f.close()
    # # sys.stdout=temp
    sys.stderr = temp
    #




