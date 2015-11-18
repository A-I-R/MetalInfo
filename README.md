MetalInfo&nbsp;——华师大商学院冶金信息研究所项目
===========
###工程目录说明
MetalInfo
>.settings----->工程相关设置<br />
>Data----->ICTCLAS分词工具所需的语料库<br />
>lib----->程序所需要的第三方库类<br />
>>NLPIR.dll----->分词包动态链接库<br />
>>jna-4.0.0.jar----->调用NLPIR.dll时所需使用的插件<br />
>>mysql-connector-java-5.1.27-bin.jar----->MySQL数据库连接器，用以操作数据库<br />

>src----->源码文件<br />
>>com
>>>ICTCLAS----->ICTCLAS接口文件包<br />
>>>>CLibrary.java----->ICTCLAS接口文件<br />

>>>air----->主程序包<br />
>>>>function----->文本处理功能包<br />
>>>>>DBConnection.java----->数据库连接类<br />
>>>>>AbstractFactory.java----->对数据库中摘要进行处理的类，进行分词并统计关键词的词频<br />
>>>>>KeywordFactory.java----->对数据库中关键词进行处理的类，统计关键词词频<br />
>>>>>AnalysisTools.java----->各类文本挖掘方法的工具类，包括TF-IDF权值计算、文本相似度计算、关键词筛选以及两字关键词的剔除功能<br />

>>>>main----->程序入口文件包<br />
>>>>>Main.java----->程序入口<br />

<br />
###工程使用方法
本程序所需要的数据储存在MySQL数据库中<br />
在使用前，请修改：
>`Main.java`中的`target`变量，将其更改为自己可以保存文件的目录<br />
>`DBConnection.java`中的数据库用户名与密码<br />
>`CLibrary.java`中`NLPIR.dll`的路径为本工程中`lib`目录下的`NLPIR.dll`<br />

<br />
###改动日志
2015.11.18<br />
>
>* 新增了关键词两个字词语的剔除功能，位于AnalysisTools类中

2015.11.14<br />
>
>* 归并了部分类并重命名了一些类，使分类更简单，避免一个方法一个类造成的冗余，方便方法与方法之间的调用
>* 新增了关键词筛选功能，位于AnalysisTools类中

2015.11.08<br />
>
>* 更新了ICTCLAS分词包的文件，原先的文件中许可证已经过期无法使用

2015.06.15<br/>
>
>* 增加文本相似度计算模块

2015.06.12<br />
>
>* 初次上传
>* 包括关键词处理、摘要分词与词频统计、TF-IDF权值计算功能
