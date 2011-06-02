
// tabla de linealizacion con resolución de 0.5 cm

#include <stdio.h>

// DEFINICION DE VARIABLES DE NORMALIZACION DE SENSORES Y MOTORES
//#define telemetroMax				740
#define telemetroMax				350 	// 350 = 0% = 35 cm // por como está configurado el hardware del robot y, al estar linealizado el telémetro, el valor MAX (800mm) es que "ve poco" y el valor MIN (60mm) es que "ve mucho". Hay que invertir esto. Además, sólo vamos a tener en cuenta los valores entre: "lineal[39] = 350" y "lineal[157] = 70". O sea, que el telemetro sólo distingue valores entre 7cm y 35cm. Si ve menor que 7cm  => ve 100%. Si ve mayor que 35cm => ve 0%.
//#define telemetroMin				0
#define telemetroMin				70 		// 70 = 100% = 7 cm // por como está configurado el hardware del robot y, al estar linealizado el telémetro, el valor MAX (800mm) es que "ve poco" y el valor MIN (60mm) es que "ve mucho". Hay que invertir esto. Además, sólo vamos a tener en cuenta los valores entre: "lineal[39] = 350" y "lineal[157] = 70". O sea, que el telemetro sólo distingue valores entre 7cm y 35cm. Si ve menor que 7cm  => ve 100%. Si ve mayor que 35cm => ve 0%.
#define telemetroMaxNormalizado		100
#define telemetroMinNormalizado		0
// las de motores están en "RAL.cpp"


unsigned int linealizar(unsigned int pos){

	unsigned int lineal[160];
	unsigned int salida;

	// primero linealizamos los telemetros con la funcion linealizar
	// que nos da la distancia entre 60 y 800 mm y luego
	// la normalizamos que el Core lo interprete bien.
	
	//los primeros 19 son 80 cm (o 800 mm que es lo mismo)
	lineal[0] = 800;
	lineal[1] = 800;
	lineal[2] = 800;
	lineal[3] = 800;
	lineal[4] = 800;
	lineal[5] = 800;
	lineal[6] = 800;
	lineal[7] = 800;
	lineal[8] = 800;
	lineal[9] = 800;

	lineal[10] = 800;
	lineal[11] = 800;
	lineal[12] = 800;
	lineal[13] = 800;
	lineal[14] = 800;
	lineal[15] = 800;
	lineal[16] = 800;
	lineal[17] = 800;
	lineal[18] = 800;
	lineal[19] = 750;

	lineal[20] = 700;
	lineal[21] = 675;
	lineal[22] = 650;
	lineal[23] = 600;
	lineal[24] = 525;
	lineal[25] = 550;
	lineal[26] = 540;
	lineal[27] = 520;
	lineal[28] = 500;
	lineal[29] = 480;

	lineal[30] = 460;
	lineal[31] = 450;
	lineal[32] = 435;
	lineal[33] = 420;
	lineal[34] = 400;
	lineal[35] = 390;
	lineal[36] = 380;
	lineal[37] = 370;
	lineal[38] = 360;
	lineal[39] = 350;

	lineal[40] = 340;
	lineal[41] = 335;
	lineal[42] = 330;
	lineal[43] = 320;
	lineal[44] = 310;
	lineal[45] = 300;
	lineal[46] = 295;
	lineal[47] = 290;
	lineal[48] = 285;
	lineal[49] = 280;

	lineal[50] = 270;
	lineal[51] = 260;
	lineal[52] = 255;
	lineal[53] = 250;
	lineal[54] = 245;
	lineal[55] = 240;
	lineal[56] = 235;
	lineal[57] = 230;
	lineal[58] = 230;
	lineal[59] = 225;

	lineal[60] = 220;
	lineal[61] = 220;
	lineal[62] = 215;
	lineal[63] = 210;
	lineal[64] = 210;
	lineal[65] = 200;
	lineal[66] = 200;
	lineal[67] = 195;
	lineal[68] = 195;
	lineal[69] = 190;

	lineal[70] = 190;
	lineal[71] = 185;
	lineal[72] = 180;
	lineal[73] = 180;
	lineal[74] = 175;
	lineal[75] = 170;
	lineal[76] = 170;
	lineal[77] = 165;
	lineal[78] = 165;
	lineal[79] = 160;

	lineal[80] = 160;
	lineal[81] = 158;
	lineal[82] = 156;
	lineal[83] = 154;
	lineal[84] = 152;
	lineal[85] = 150;
	lineal[86] = 148;
	lineal[87] = 146;
	lineal[88] = 144;
	lineal[89] = 142;

	lineal[90] = 140;
	lineal[91] = 140;
	lineal[92] = 138;
	lineal[93] = 136;
	lineal[94] = 134;
	lineal[95] = 132;
	lineal[96] = 130;
	lineal[97] = 130;
	lineal[98] = 128;
	lineal[99] = 126;

	lineal[100] = 124;
	lineal[101] = 122;
	lineal[102] = 120;
	lineal[103] = 119;
	lineal[104] = 118;
	lineal[105] = 117;
	lineal[106] = 116;
	lineal[107] = 115;
	lineal[108] = 114;
	lineal[109] = 113;

	lineal[110] = 112;
	lineal[111] = 111;
	lineal[112] = 110;
	lineal[113] = 109;
	lineal[114] = 107;
	lineal[115] = 106;
	lineal[116] = 105;
	lineal[117] = 104;
	lineal[118] = 103;
	lineal[119] = 102;

	lineal[120] = 101;
	lineal[121] = 100;
	lineal[122] = 100;
	lineal[123] = 99;
	lineal[124] = 98;
	lineal[125] = 97;
	lineal[126] = 96;
	lineal[127] = 95;
	lineal[128] = 94;
	lineal[129] = 93;

	lineal[130] = 92;
	lineal[131] = 91;
	lineal[132] = 90;
	lineal[133] = 90;
	lineal[134] = 90;
	lineal[135] = 89;
	lineal[136] = 88;
	lineal[137] = 87;
	lineal[138] = 86;
	lineal[139] = 85;

	lineal[140] = 84;
	lineal[141] = 83;
	lineal[142] = 82;
	lineal[143] = 81;
	lineal[144] = 80;
	lineal[145] = 80;
	lineal[146] = 80;
	lineal[147] = 79;
	lineal[148] = 78;
	lineal[149] = 77;

	lineal[150] = 76;
	lineal[151] = 75;
	lineal[152] = 75;
	lineal[153] = 74;
	lineal[154] = 73;
	lineal[155] = 72;
	lineal[156] = 71;
	lineal[157] = 70;
	lineal[158] = 70;
	lineal[159] = 60;

	// OJO: al estar linealizado el telémetro, el valor MAX (800mm) es que ve poco
	//		y el valor MIN (60mm) es que ve mucho,
	//		entonces, hay que invertir esto!!

	// además, sólo vamos a tener en cuenta los valores entre: "lineal[39] = 350" y "lineal[157] = 70"
	// osea, que el telemetro sólo distingue valores entre 7cm y 35cm
	// 		=> si ve menor que 7cm  => ve 100%
	//  	=> si ve mayor que 35cm => ve 0%

	if( pos > 157 ){
		salida = telemetroMaxNormalizado;
	}
	//else if( pos < 28 ){ // esto era para "lineal[28] = 500", que viera hasta 50cm...
	else if( pos < 39 ){
			salida = telemetroMinNormalizado;
		 }
		 else{
				//salida = (unsigned int)((float)(lineal[pos] - 60))/(7.4*100/1024); // normalizado de 0:1024
				// normalizado de 0:1024 teniendo en cuenta de invertir el valor...
				//salida = (unsigned int)(( (float)(100-((float)(((float)((lineal[pos]-60)*100))/telemetroMax)))*telemetroMaxNormalizado)/100) ;
				//salida = (unsigned int)(( (float)(100-((float)(((float)((lineal[pos]-70)*100))/telemetroMax)))*telemetroMaxNormalizado)/100) ;
				salida = (unsigned int)(( (float)(100-((float)(((float)((lineal[pos]-telemetroMin)*100))/telemetroMax)))*telemetroMaxNormalizado)/100) ;
		 }
	//printf("-> Linealizo - valor entrada: %d  - valor salida: %d \n", pos, salida);
	return salida;
}
