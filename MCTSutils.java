package org.ggp.base.player.gamer.statemachine.sample.gpp_player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class MCTSutils {

	public static String hash(String str,int lengthOfHash){
		byte[] ascii=str.getBytes();

		int[][]  sliceAscii=new int[ascii.length/lengthOfHash+1][lengthOfHash];
		int k=0;
		for(int i=0;i<(ascii.length/lengthOfHash)+1;i++) {
			for(int j=0;j<lengthOfHash;j++) {
				if(k<ascii.length)
					sliceAscii[i][j]=ascii[k++];
				else
					sliceAscii[i][j]=0;
			}
		}
		int[] mixAscii=new int[lengthOfHash];
		for(int i=0;i<mixAscii.length;i++) {
			for(int j=0;j<sliceAscii.length;j++) {
				mixAscii[i]+=(int)(Math.pow(sliceAscii[j][i],Math.log(j+2)))%256;
			}

			mixAscii[i]%=256;
		}

		String result="";
		for(int i=0;i<mixAscii.length;i++) {
			if(mixAscii[i]<16)
				result+=Integer.toString(0, 16);

			result+=Integer.toString(mixAscii[i], 16);

		}
		return result;
	}

	public static long hashInteger(String str,int lengthOfHash){
		byte[] ascii=str.getBytes();

		long[][]  sliceAscii=new long[ascii.length/lengthOfHash+1][lengthOfHash];
		int k=0;
		for(int i=0;i<(ascii.length/lengthOfHash)+1;i++) {
			for(int j=0;j<lengthOfHash;j++) {
				if(k<ascii.length)
					sliceAscii[i][j]=ascii[k++];
				else
					sliceAscii[i][j]=0;
			}
		}
		long[] mixAscii=new long[lengthOfHash];
		for(int i=0;i<mixAscii.length;i++) {
			for(int j=0;j<sliceAscii.length;j++) {
				//System.out.print(sliceAscii[j][i]+":"+(long)(Math.pow(sliceAscii[j][i],Math.log(j+2))*10)+" ");
				mixAscii[i]+=(long)(Math.pow(sliceAscii[j][i],Math.log(j+2))*10);
			}
			//System.out.println();

			if(mixAscii[i]<0)
				System.out.println(mixAscii[i]);
			mixAscii[i]%=512;
		}
		//System.out.println();

		long result=0;
		double apple=1;
		for(int i=0;i<mixAscii.length;i++) {
			apple=Math.pow(10,i*3);
			//System.out.println(mixAscii[i]*apple);
			result += mixAscii[i]*apple;
		}
		return result;

	}

	public static void testPrint(int[] a) {
		for(int i=0;i<a.length;i++) {
			System.out.println(i+":"+a[i]);
		}
	}

	public static String preprocess(String str) {
		str=str.replace("true", "");//.replace(" ", "");//.replace("cell", "");
		char[] c =str.toCharArray();
		String s="";
		ArrayList<String> ls=new ArrayList<String>();
		for(int i=0;i<c.length;i++) {
			if(c[i]=='(') {
				while(c[i++]=='(');
				int j;
				for(j=i+1;c[j]!=')';j++) {
					s+=c[j];
				}
				s+=c[j];

				ls.add(s);
				s="";
				i=j;
			}
		}

		Collections.sort(ls);

		for(String apple:ls) {
			s+=apple;
		}


		return s;
	}



	public static String pressRoleString(String str,Map<String,String> map,Map<String,Integer> appearMap) {
		char[] c =str.toCharArray();
		String s="";
		ArrayList<String> ls=new ArrayList<String>();
		for(int i=0;i<c.length;i++) {
			if(c[i]=='(') {
				int j;
				for(j=i;c[j]!=')';j++) {
					s+=c[j];
				}
				s+=c[j];
				String[] array=s.split(" ");
				s="";
				for(int k=0;k<array.length;k++) {
					if(k==1)
						continue;
					if(1<k && k<array.length-1) {
						int begin=0;
						String ccc="";
						boolean flag=false;
						while(begin<array[k].length()) {
							ccc=array[k].substring(begin,begin+1);
							if(!map.containsKey(ccc)) {
								map.put(ccc,array[k]);
								appearMap.put(ccc,1);
								flag=true;
								break;
							}else {
								if(array[k].equals(map.get(ccc))) {
									int count=appearMap.get(ccc);
									appearMap.put(ccc,count+1);
									flag=true;
									break;
								}
							}
							begin++;
						}
						if(flag)
							array[k]=ccc;

					}

					s+=array[k];
					if(1<k && k<array.length-2)
						s+=",";


				}
				ls.add(s);
				s="";
				i=j;
			}
		}

		for(String apple:ls) {
			s+=apple;

		}
		return s;
	}

	public static void searchBoardSize(String str,int[] xy) {
		char c[]=str.toCharArray();
		String s="";
		int maxX=xy[0],maxY=xy[1];
		int minX=xy[2],minY=xy[3];
		for(int i=0;i<c.length;i++) {
			if(c[i]=='(') {
				int j;
				for(j=i+2;c[j]!=')';j++) {
					s+=c[j];
				}
				//s+=c[j];
				String[] array=s.split(" ");
				s="";
				//System.out.println("----");
				/*
				for(int m=0;m<array.length;m++) {
					System.out.println(array[m]);
				}*/
				if(array.length==4) {
					int x=Integer.valueOf(array[1]).intValue();
					if(maxX<x) {
						maxX=x;
					}
					int y=Integer.valueOf(array[2]).intValue();
					if(maxY<y) {
						maxY=y;
					}
					if(minX>x) {
						minX=x;
					}
					if(minY>y) {
						minY=y;
					}
				}
			}
		}
		//System.out.println("max x---"+maxX+", max Y---"+maxY);
		xy[0]=maxX;
		xy[1]=maxY;
		xy[2]=minX;
		xy[3]=minY;
	}


	public static String encodeHaffman(String state,Map<String,String> map) {
		char c[]=state.toCharArray();
		String result="",str="";
		for(int i=0;i<c.length;i++) {
			str+=map.get(String.valueOf(c[i]));
			if(str.length()>4) {
				String binary=str.substring(0,4);
				//result+=Integer.toHexString(Integer.parseInt(binary, 2));
				result+=binary;
				str=str.substring(4);
			}
		}
		if(str.length()>0) {
			for(int i=str.length();i<4;i++)
				str+="0";
		//	result+=Integer.toHexString(Integer.parseInt(str, 2));
			result+=str;
		}
		return result;
	}

	public static String makePerfectBoard(String state,int[] boardRange) {
		char c[]=state.toCharArray();
		String result="";
		int x=boardRange[2],y=boardRange[3]-1;
		for(int i=0;i<c.length;i++) {

			String s="";
			int j=0;
			if(c[i]=='(') {
				for(j=i+1;c[j]!=')';j++) {
					s+=c[j];
				}
			}
			String[] array=s.split(",");
			if(array.length==1) {
				if(boardRange[2]<=x && x<=boardRange[0] && boardRange[3]<=y && y<=boardRange[1]) {
					result+="¥";
					i--;
					y++;
					//continue;
				}else {
					result+=array[0];
					i=j;
				}
			}
			else if(array.length==3) {
				if(y<boardRange[3])
					y++;
				if(x==Integer.valueOf(array[0]).intValue() && y==Integer.valueOf(array[1]).intValue()) {
					result+=array[2];
					i=j;
					y++;
				}
				else {
					result+="¥";
					y++;
					i--;
				}
			}
			if(y>boardRange[1]) {
				y=boardRange[3];
				x++;
			}
			if(x>boardRange[0]&&i+1>c.length)
				break;
		}
		if(y<boardRange[3]) {
			for(int j=boardRange[3];j<=boardRange[1];j++) {
				for(int i=boardRange[2];i<=boardRange[0];i++) {
					result+="¥";
				}
			}
		}
		return result;
	}

}
