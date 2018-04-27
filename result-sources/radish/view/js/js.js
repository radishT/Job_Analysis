var dataMap = {};
function dataFormatter(obj) {
    var pList = ['北京','天津','河北','山西','内蒙古','辽宁','吉林','黑龙江','上海','江苏','浙江','安徽','福建','江西','山东','河南','湖北','湖南','广东','广西','海南','重庆','四川','贵州','云南','西藏','陕西','甘肃','青海','宁夏','新疆'];
    var temp;
    for (var year = 2018; year <= 2018; year++) {
        var max = 0;
        var sum = 0;
        temp = obj[year];
        for (var i = 0, l = temp.length; i < l; i++) {
            max = Math.max(max, temp[i]);
            sum += temp[i];
            obj[year][i] = {
                name : pList[i],
                value : temp[i]
            }
        }
        obj[year + 'max'] = Math.floor(max / 100) * 100;
        obj[year + 'sum'] = sum;
    }
    return obj;
}

dataMap.javadata1 = dataFormatter({
    //max : 60000,
    2018:[994,419,596,336,155,624,314,301,750,2191,999,367,1099,388,888,592,599,1096,1669,246,113,30,366,295,295,7,590,106,28,55,135]
});
dataMap.javadata2 = dataFormatter({
    //max : 4000,
    2018:[82.44,84.21,956.84,197.8,374.69,590.2,446.17,474.2,79.68,1110.44,685.2,783.66,664.78,535.98,1390,1288.36,707,847.25,1015.08,601.99,222.89,317.87,1047.95,281.1,463.44,39.75,282.21,215.51,47.31,52.95,305]
});
dataMap.javadata1=dataFormatter({2018:[994,419,596,336,155,624,314,301,750,2191,999,367,1099,388,888,592,599,1096,1669,246,113,30,366,295,295,7,590,106,28,55,135]});
dataMap.javadata2=dataFormatter({2018:[21764.59,10559.67,8471.48,7636.9,7425.81,8369.39,7033.44,7458.47,17978.67,11696.71,11914.41,9843.32,11484.53,7956.19,8363.74,8919.76,11954.09,10407.85,14360.1,8768.29,9088.5,9700.0,12265.03,8766.1,8584.75,8642.86,11192.37,7830.19,7928.57,7345.45,8633.33]});
dataMap.javadata3=dataFormatter({2018:[27463.78,13226.73,10619.13,9473.21,9406.45,10479.17,8789.81,9172.76,22457.33,14712.46,15050.05,12247.96,14495.91,10046.39,10451.58,11150.34,14989.98,12933.39,18004.19,10995.93,11345.13,12166.67,15330.6,10867.8,10654.24,10000.0,14125.42,9820.75,10142.86,9145.45,10666.67]});
dataMap.javadata4=dataFormatter({2018:[16065.39,7892.6,6323.83,5800.6,5445.16,6259.62,5277.07,5744.19,13500.0,8680.97,8778.78,7438.69,8473.16,5865.98,6275.9,6689.19,8918.2,7882.3,10716.0,6540.65,6831.86,7233.33,9199.45,6664.41,6515.25,7285.71,8259.32,5839.62,5714.29,5545.45,6600.0]});
dataMap.javadata5=dataFormatter({2018:[0.88,0.68,0.46,0.58,0.66,0.55,0.57,0.53,0.75,0.63,0.58,0.61,0.55,0.51,0.53,0.51,0.68,0.52,0.6,0.5,0.65,0.57,0.62,0.66,0.53,0.57,0.73,0.55,0.64,0.6,0.54]});
dataMap.javadata6=dataFormatter({2018:[0.17,0.38,0.45,0.4,0.52,0.41,0.53,0.49,0.21,0.35,0.47,0.44,0.4,0.51,0.47,0.36,0.29,0.25,0.31,0.41,0.47,0.37,0.32,0.48,0.53,0.29,0.27,0.58,0.5,0.45,0.55]});
dataMap.cdata1=dataFormatter({2018:[436,263,172,6,11,425,66,2,404,854,635,245,606,8,557,319,238,141,971,64,23,171,384,26,64,2,267,2,1,4,13]});
dataMap.cdata2=dataFormatter({2018:[14083.72,8471.48,7229.65,6833.33,7181.82,8261.18,7242.42,6500.0,13647.28,10152.22,10826.77,9383.67,9854.79,7125.0,8529.62,8716.3,9405.46,9127.66,10754.38,7046.88,7456.52,8418.13,10277.34,7269.23,8242.19,9000.0,9174.16,6500.0,3500.0,7625.0,8038.46]});
dataMap.cdata3=dataFormatter({2018:[17729.36,10608.37,9069.77,8666.67,8818.18,10294.12,9151.52,8500.0,17178.22,12761.12,13634.65,11730.61,12438.94,8750.0,10673.25,10827.59,11798.32,11326.24,13568.49,8765.63,9260.87,10608.19,12861.98,9038.46,10312.5,10000.0,11531.84,8500.0,5000.0,9250.0,10153.85]});
dataMap.cdata4=dataFormatter({2018:[10438.07,6334.6,5389.53,5000.0,5545.45,6228.24,5333.33,4500.0,10116.34,7543.33,8018.9,7036.73,7270.63,5500.0,6386.0,6605.02,7012.61,6929.08,7940.27,5328.13,5652.17,6228.07,7692.71,5500.0,6171.88,8000.0,6816.48,4500.0,2000.0,6000.0,5923.08]});
dataMap.cdata5=dataFormatter({2018:[0.72,0.59,0.44,0.5,0.27,0.45,0.59,0.5,0.62,0.53,0.53,0.61,0.38,0.38,0.46,0.36,0.49,0.35,0.43,0.36,0.3,0.44,0.52,0.42,0.47,0.0,0.64,0.5,1.0,0.5,0.31]});
dataMap.cdata6=dataFormatter({2018:[0.4,0.48,0.41,0.5,0.36,0.4,0.45,0.0,0.44,0.52,0.52,0.51,0.5,0.63,0.5,0.49,0.5,0.52,0.53,0.58,0.61,0.53,0.5,0.73,0.61,0.0,0.4,1.0,1.0,0.5,0.69]});
dataMap.linuxdata1=dataFormatter({2018:[697,307,460,17,104,518,185,6,179,957,688,331,682,107,784,361,350,330,1650,207,80,300,352,209,152,22,339,59,19,44,93]});
dataMap.linuxdata2=dataFormatter({2018:[20027.98,10506.51,8125.0,6941.18,6865.38,9862.93,7364.86,8583.33,17500.0,11242.95,12292.88,9965.26,11005.87,7523.36,9188.14,8860.11,11292.86,10207.58,12750.3,8978.26,8643.75,10453.33,12170.45,8607.66,8434.21,7090.91,11467.55,7322.03,7052.63,7488.64,7629.03]});
dataMap.linuxdata3=dataFormatter({2018:[25428.98,13172.64,10136.96,8705.88,8557.69,12158.3,9205.41,10833.33,22162.01,14208.99,15475.29,12522.66,13806.45,9308.41,11562.5,11077.56,14220.0,12742.42,16087.88,11309.18,10637.5,13140.0,15292.61,10741.63,10388.16,8545.45,14486.73,9016.95,8947.37,9590.91,9376.34]});
dataMap.linuxdata4=dataFormatter({2018:[14626.97,7840.39,6113.04,5176.47,5173.08,7567.57,5524.32,6333.33,12837.99,8276.91,9110.47,7407.85,8205.28,5738.32,6813.78,6642.66,8365.71,7672.73,9412.73,6647.34,6650.0,7766.67,9048.3,6473.68,6480.26,5636.36,8448.38,5627.12,5157.89,5386.36,5881.72]});
dataMap.linuxdata5=dataFormatter({2018:[0.81,0.66,0.45,0.41,0.49,0.66,0.56,0.5,0.71,0.58,0.59,0.58,0.51,0.43,0.54,0.37,0.62,0.5,0.56,0.45,0.51,0.54,0.65,0.64,0.49,0.41,0.77,0.59,0.47,0.48,0.55]});
dataMap.linuxdata6=dataFormatter({2018:[0.27,0.49,0.43,0.59,0.55,0.38,0.54,0.67,0.25,0.45,0.47,0.52,0.5,0.59,0.46,0.44,0.41,0.4,0.47,0.46,0.51,0.42,0.4,0.56,0.57,0.59,0.35,0.54,0.58,0.64,0.55]});
dataMap.pythondata1=dataFormatter({2018:[300,263,124,55,20,378,38,78,392,835,528,317,302,99,355,89,349,306,901,48,29,220,308,63,50,2,335,7,1,4,26]});
dataMap.pythondata2=dataFormatter({2018:[19906.67,11363.12,8475.81,7727.27,8225.0,9935.19,8315.79,9185.9,17931.12,12986.83,15310.61,11348.58,11470.2,8333.33,10349.3,9949.44,12750.72,11196.08,14235.85,11791.67,10017.24,11611.36,12951.3,9896.83,10950.0,10000.0,12594.03,6714.29,7500.0,8125.0,10019.23]});
dataMap.pythondata3=dataFormatter({2018:[25460.0,14288.97,10532.26,9672.73,10350.0,12423.28,10315.79,11217.95,22609.69,16513.77,19464.02,14441.64,14417.22,10393.94,13064.79,12483.15,16240.69,14068.63,18011.1,15125.0,12448.28,14722.73,16334.42,12285.71,13320.0,12000.0,16074.63,7857.14,10000.0,10750.0,12500.0]});
dataMap.pythondata4=dataFormatter({2018:[14353.33,8437.26,6419.35,5781.82,6100.0,7447.09,6315.79,7153.85,13252.55,9459.88,11157.2,8255.52,8523.18,6272.73,7633.8,7415.73,9260.74,8323.53,10460.6,8458.33,7586.21,8500.0,9568.18,7507.94,8580.0,8000.0,9113.43,5571.43,5000.0,5500.0,7538.46]});
dataMap.pythondata5=dataFormatter({2018:[0.83,0.82,0.45,0.78,0.7,0.73,0.61,0.67,0.82,0.67,0.71,0.76,0.56,0.65,0.63,0.6,0.76,0.59,0.66,0.58,0.55,0.63,0.73,0.75,0.52,0.0,0.8,0.43,0.0,1.0,0.62]});
dataMap.pythondata6=dataFormatter({2018:[0.36,0.47,0.43,0.42,0.75,0.4,0.42,0.44,0.39,0.39,0.51,0.44,0.46,0.6,0.43,0.44,0.4,0.39,0.47,0.44,0.59,0.4,0.38,0.51,0.48,1.0,0.32,0.57,0.0,1.0,0.5]});
dataMap.webdata1=dataFormatter({2018:[745,341,489,307,103,636,257,39,120,1370,1401,365,713,364,998,404,541,342,2663,207,112,30,707,238,267,4,427,66,9,32,59]});
dataMap.webdata2=dataFormatter({2018:[18572.48,8282.99,6674.85,6778.5,5990.29,7473.27,6250.97,6512.82,13766.67,9156.57,11143.83,8538.36,8714.59,7328.3,7329.16,6997.52,9791.13,8149.12,11408.56,7557.97,8093.75,8183.33,10073.55,8048.32,7569.29,7250.0,9241.22,7507.58,8944.44,6734.38,7177.97]});
dataMap.webdata3=dataFormatter({2018:[23524.83,10348.97,8370.14,8384.36,7495.15,9319.18,7821.01,8051.28,17175.0,11546.72,14066.38,10753.42,10955.12,9162.09,9174.35,8727.72,12316.08,10146.2,14328.58,9381.64,10053.57,10233.33,12736.92,10079.83,9423.22,8000.0,11665.11,9151.52,11444.44,8593.75,8796.61]});
dataMap.webdata4=dataFormatter({2018:[13620.13,6217.01,4979.55,5172.64,4485.44,5627.36,4680.93,4974.36,10358.33,6766.42,8221.27,6323.29,6474.05,5494.51,5483.97,5267.33,7266.17,6152.05,8488.55,5734.3,6133.93,6133.33,7410.18,6016.81,5715.36,6500.0,6817.33,5863.64,6444.44,4875.0,5559.32]});
dataMap.webdata5=dataFormatter({2018:[0.84,0.56,0.36,0.57,0.42,0.55,0.49,0.44,0.72,0.5,0.53,0.58,0.42,0.39,0.45,0.38,0.55,0.37,0.48,0.37,0.54,0.57,0.55,0.57,0.44,0.5,0.65,0.5,0.78,0.44,0.54]});
dataMap.webdata6=dataFormatter({2018:[0.28,0.54,0.55,0.51,0.66,0.49,0.59,0.56,0.45,0.53,0.53,0.56,0.58,0.6,0.58,0.57,0.42,0.51,0.47,0.55,0.49,0.63,0.43,0.55,0.56,0.5,0.4,0.61,0.22,0.56,0.66]});
dataMap.cppdata1=dataFormatter({2018:[209,8,4,2,0,0,0,3,15,44,274,25,53,3,14,1,124,42,454,1,1,21,181,2,1,0,45,0,0,0,0]});
dataMap.cppdata2=dataFormatter({2018:[22866.03,10687.5,8000.0,4750.0,0.0,0.0,0.0,9333.33,19133.33,13500.0,16945.26,11400.0,13141.51,9833.33,9071.43,11500.0,12516.13,11535.71,18411.89,7000.0,1500.0,11928.57,12447.51,9000.0,8000.0,0.0,12088.89,0.0,0.0,0.0,0.0]});
dataMap.cppdata3=dataFormatter({2018:[29071.77,13875.0,10250.0,5500.0,0.0,0.0,0.0,11333.33,24200.0,17431.82,21653.28,14760.0,16735.85,11666.67,11571.43,15000.0,15903.23,14809.52,23484.58,8000.0,2000.0,15190.48,15585.64,11500.0,10000.0,0.0,15644.44,0.0,0.0,0.0,0.0]});
dataMap.cppdata4=dataFormatter({2018:[16660.29,7500.0,5750.0,4000.0,0.0,0.0,0.0,7333.33,14066.67,9568.18,12237.23,8040.0,9547.17,8000.0,6571.43,8000.0,9129.03,8261.9,13339.21,6000.0,1000.0,8666.67,9309.39,6500.0,6000.0,0.0,8533.33,0.0,0.0,0.0,0.0]});
dataMap.cppdata5=dataFormatter({2018:[0.91,0.75,0.5,0.5,0.0,0.0,0.0,0.67,0.87,0.59,0.76,0.8,0.64,1.0,0.64,0.0,0.67,0.64,0.74,0.0,0.0,0.48,0.63,1.0,0.0,0.0,0.78,0.0,0.0,0.0,0.0]});
dataMap.cppdata6=dataFormatter({2018:[0.27,0.63,0.75,0.5,0.0,0.0,0.0,0.0,0.73,0.41,0.36,0.24,0.42,0.33,0.64,0.0,0.34,0.26,0.31,0.0,0.0,0.48,0.35,0.5,1.0,0.0,0.33,0.0,0.0,0.0,0.0]});
dataMap.androiddata1=dataFormatter({2018:[450,0,7,3,1,0,1,0,434,198,461,62,2,13,34,2,178,56,915,0,4,0,4,7,12,0,74,1,0,0,0]});
dataMap.androiddata2=dataFormatter({2018:[23723.33,0.0,7500.0,6166.67,8000.0,0.0,9500.0,0.0,18748.85,13020.2,15767.9,9467.74,7000.0,8730.77,8455.88,5750.0,10828.65,10535.71,14014.75,0.0,8250.0,0.0,7750.0,8142.86,9791.67,0.0,10250.0,9000.0,0.0,0.0,0.0]});
dataMap.androiddata3=dataFormatter({2018:[30215.56,0.0,9857.14,7666.67,9000.0,0.0,12000.0,0.0,23658.99,16585.86,20060.74,12080.65,8500.0,10923.08,10852.94,7000.0,13775.28,13285.71,17634.97,0.0,10500.0,0.0,9250.0,10000.0,12500.0,0.0,13040.54,12000.0,0.0,0.0,0.0]});
dataMap.androiddata4=dataFormatter({2018:[17231.11,0.0,5142.86,4666.67,7000.0,0.0,7000.0,0.0,13838.71,9454.55,11475.05,6854.84,5500.0,6538.46,6058.82,4500.0,7882.02,7785.71,10394.54,0.0,6000.0,0.0,6250.0,6285.71,7083.33,0.0,7459.46,6000.0,0.0,0.0,0.0]});
dataMap.androiddata5=dataFormatter({2018:[0.94,0.0,0.14,0.33,0.0,0.0,1.0,0.0,0.79,0.6,0.72,0.58,0.5,0.38,0.29,0.0,0.61,0.55,0.63,0.0,0.5,0.0,0.5,0.57,0.17,0.0,0.65,0.0,0.0,0.0,0.0]});
dataMap.androiddata6=dataFormatter({2018:[0.14,0.0,0.29,1.0,0.0,0.0,0.0,0.0,0.27,0.46,0.39,0.52,1.0,0.54,0.68,0.5,0.38,0.36,0.42,0.0,0.75,0.0,0.25,0.43,0.5,0.0,0.35,1.0,0.0,0.0,0.0]});

option = {
    baseOption: {
        timeline: {
            // y: 0,
            axisType: 'category',
            // realtime: false,
            // loop: false,
            autoPlay: false,
            // currentIndex: 2,
            playInterval: 1000,
            // controlStyle: {
            //     position: 'left'
            // },
            data: [
                'java','C#','linux','python','web','C++','android'
            ],
            label: {
                formatter : function(s) {
                    return s;
                }
            }
        },
        title: {
            subtext: '数据来自第八组萝卜中队'
        },
        tooltip: {
            
        },
        legend: {
            x: 'right',
            data: ['岗位需求量', '平均薪资', '最高薪资', '最低薪资', '本科及以上员工比例', '工作经验不限的比例'],
            //show:false
        },
        calculable : true,
        grid: {
            top: 80,
            bottom: 100,
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow',
                    label: {
                        show: true,
                        formatter: function (params) {
                            return params.value.replace('\n', '');
                        }
                    }
                }
            }
        },
        xAxis: [
            {
                'type':'category',
                'axisLabel':{'interval':0},
                'data':[
                    '北京','\n天津','河北','\n山西','内蒙古','\n辽宁','吉林','\n黑龙江',
                    '上海','\n江苏','浙江','\n安徽','福建','\n江西','山东','\n河南',
                    '湖北','\n湖南','广东','\n广西','海南','\n重庆','四川','\n贵州',
                    '云南','\n西藏','陕西','\n甘肃','青海','\n宁夏','新疆'
                ],
                splitLine: {show: true}
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: ''
            }
        ],
        series: [
            {name: '岗位需求量', type: 'bar'},
            {name: '平均薪资', type: 'bar'},
            {name: '最高薪资', type: 'bar'},
            {name: '最低薪资', type: 'bar'},
            {name: '本科及以上员工比例', type: 'bar'},
            {name: '工作经验不限的比例', type: 'bar'},
        ]
    },
    options: [
        {
            title: {text: '2018-4-java'},
            series: [
                {data: dataMap.javadata1['2018']},// menu4
                {data: dataMap.javadata2['2018']},// menu5
                {data: dataMap.javadata3['2018']},//menu6
                {data: dataMap.javadata4['2018']},// menu1
                {data: dataMap.javadata5['2018']},// menu2
                {data: dataMap.javadata6['2018']},// menu3
            ]
        },
        {
            title: {text: '2018-4-C#'},
            series: [
                {data: dataMap.cdata1['2018']},// menu4
                {data: dataMap.cdata2['2018']},// menu5
                {data: dataMap.cdata3['2018']},//menu6
                {data: dataMap.cdata4['2018']},// menu1
                {data: dataMap.cdata5['2018']},// menu2
                {data: dataMap.cdata6['2018']},// menu3
            ]
        },
        {
            title: {text: '2018-4-linux'},
            series: [
                {data: dataMap.linuxdata1['2018']},// menu4
                {data: dataMap.linuxdata2['2018']},// menu5
                {data: dataMap.linuxdata3['2018']},//menu6
                {data: dataMap.linuxdata4['2018']},// menu1
                {data: dataMap.linuxdata5['2018']},// menu2
                {data: dataMap.linuxdata6['2018']},// menu3
            ]
        },
        {
            title: {text: '2018-4-python'},
            series: [
                {data: dataMap.pythondata1['2018']},// menu4
                {data: dataMap.pythondata2['2018']},// menu5
                {data: dataMap.pythondata3['2018']},//menu6
                {data: dataMap.pythondata4['2018']},// menu1
                {data: dataMap.pythondata5['2018']},// menu2
                {data: dataMap.pythondata6['2018']},// menu3
            ]
        },
        {
            title: {text: '2018-4-web'},
            series: [
                {data: dataMap.webdata1['2018']},// menu4
                {data: dataMap.webdata2['2018']},// menu5
                {data: dataMap.webdata3['2018']},//menu6
                {data: dataMap.webdata4['2018']},// menu1
                {data: dataMap.webdata5['2018']},// menu2
                {data: dataMap.webdata6['2018']},// menu3
            ]
        },
        {
            title: {text: '2018-4-C++'},
            series: [
                {data: dataMap.cppdata1['2018']},// menu4
                {data: dataMap.cppdata2['2018']},// menu5
                {data: dataMap.cppdata3['2018']},//menu6
                {data: dataMap.cppdata4['2018']},// menu1
                {data: dataMap.cppdata5['2018']},// menu2
                {data: dataMap.cppdata6['2018']},// menu3
            ]
        },
        {
            title: {text: '2018-4-Android'},
            series: [
                {data: dataMap.androiddata1['2018']},// menu4
                {data: dataMap.androiddata2['2018']},// menu5
                {data: dataMap.androiddata3['2018']},//menu6
                {data: dataMap.androiddata4['2018']},// menu1
                {data: dataMap.androiddata5['2018']},// menu2
                {data: dataMap.androiddata6['2018']},// menu3
            ]
        }
    ]
};
$(function () {
    chartOutChar = echarts.init(document.getElementById('showChart'));
    chartOutChar.setOption(option);
  
});

// dispatchAction({
//     type: 'legendSelect',
//     // 图例名称
//     name: string
// })