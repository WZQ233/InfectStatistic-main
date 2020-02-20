/**
 * 
 * @author wzzzq
 *
 */
import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.text.SimpleDateFormat;


public class InfectStatistic {
	public String[] args;		//��������
	public String logPath;		//������־·��
	public String outPath;		//�������·��
	boolean typeIsExist;
	boolean provinceIsExist;
	public int[] type = {1,2,3,4};		  //������������˳��
	public int[] province = new int[35];  //������ʡ��
	public List<String> types = new ArrayList<String>();
	public List<String> provinces = new ArrayList<String>();
	public String[] typeStr = {"��Ⱦ����","���ƻ���","����","����"};	//�������ͣ�ip��sp��cure��dead��
	public String[] provinceStr = {"ȫ��", "����", "����" ,"����", "����", "����","����",
			"�㶫", "����", "����", "����", "�ӱ�", "����", "������", "����", "����", "����",
			"����", "����", "����", "���ɹ�", "����", "�ຣ", "ɽ��", "ɽ��", "����", "�Ϻ�",
			"�Ĵ�", "̨��", "���", "����", "���", "�½�", "����", "�㽭"};	//����ʡ��
	public LinkedHashMap<String,Integer> ip = new LinkedHashMap<String,Integer>();	//�����ʡ�ĸ�Ⱦ��������
	public LinkedHashMap<String,Integer> sp = new LinkedHashMap<String,Integer>(); 	//�����ʡ�����ƻ�������
	public LinkedHashMap<String,Integer> cure = new LinkedHashMap<String,Integer>();	//�����ʡ����������
	public LinkedHashMap<String,Integer> dead = new LinkedHashMap<String,Integer>();	//�����ʡ����������
	 // ����������ʽ�����ʽ�ڵĿո񲻿������޸ã������Ӱ���ȡ����
    String s1 = "\\s*\\S+ ���� ��Ⱦ���� \\d+��\\s*";
    String s2 = "\\s*\\S+ ���� ���ƻ��� \\d+��\\s*";
    String s3 = "\\s*\\S+ ��Ⱦ���� ���� \\S+ \\d+��\\s*";
    String s4 = "\\s*\\S+ ���ƻ��� ���� \\S+ \\d+��\\s*";
    String s5 = "\\s*\\S+ ���� \\d+��\\s*";
    String s6 = "\\s*\\S+ ���� \\d+��\\s*";
    String s7 = "\\s*\\S+ ���ƻ��� ȷ���Ⱦ \\d+��\\s*";
    String s8 = "\\s*\\S+ �ų� ���ƻ��� \\d+��\\s*";
	//�趨���ڸ�ʽ
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	Date d = new Date(System.currentTimeMillis());
	public String date = dateFormat.format(d);
	
	public InfectStatistic() {
		for(int i = 0;i < provinceStr.length;i++) {
			ip.put(provinceStr[i], 0);
			sp.put(provinceStr[i], 0);
			cure.put(provinceStr[i], 0);
			dead.put(provinceStr[i], 0);
		}
	}
	
	//�����������
	public boolean inspectParameter(String [] argsStr) {
		int j;
		args = argsStr;
		if(!argsStr[0].equals("list")) {
			System.out.println("Command line format error.");
			return false;
		}
		for(j = 1;j < argsStr.length;j++) {
			switch(argsStr[j]) {
				case "-log":
					j = inspectLogPath(++j);
					if(j == -1) {
						System.out.println("Command line format error.('-log'parameter)");
						return false;
					}
					else{
						logPath = argsStr[j];
						break;
					}
				case "-date":
					j = inspectDate(++j);
					if(j == -1) {
						System.out.println("Command line format error.('-date'parameter)");
						return false;
					}
					else{
						date = args[j] + ".log.txt";
						break;
					}
				case "-out":
					j = inspectOutPath(++j);
					if(j == -1) {
						System.out.println("Command line format error.('-out'parameter)");
						return false;
					}
					else{
						outPath = argsStr[j];
						break;
					}
				case "-type":
					j = inspectType(++j);
					if(j == -1) {
						System.out.println("Command line format error.('-type'parameter)");
						return false;
					}
					else
						break;
				case "-province":
					j = inspectProvince(++j); 
					if(j == -1) {
						System.out.println("Command line format error.('-province'parameter)");
						return false;
					}
					else  
						break;
				 default:
					 System.out.println("Unknown error.");
					 return false;
			}
			
		}
		if (types.isEmpty()) {
            types.add("ip");
            types.add("sp");
            types.add("cure");
            types.add("dead");
        }
		return true;
	}
	
	//����·��
	public int inspectLogPath(int j) {
		if (j != args.length && args[j].matches("^[A-z]:\\\\(.+?\\\\)*$")
				|| (args[j] + "\\").matches("^[A-z]:\\\\(.+?\\\\)*$")) 
				return j;
			else
				return -1;
		
		
	}
	//�������·��
	public int inspectOutPath(int j) {
		if (j != args.length && args[j].matches("^[A-z]:\\\\(\\S+)+(\\.txt)$"))
			return j;
		else return -1;
	}
	
	//��������
	public int inspectDate(int j) {
		if(j < args.length) {
			if(isValidDate(args[j])) {
				if(date.compareTo(args[j]) >= 0)
					return j;
				else 
					return -1;
			}
			else
				return -1;
		}
		else
			return -1;
	}
	
	//��������
	public int inspectType(int j) {
		typeIsExist = true;
		if(j < args.length) {
			int k,n = j,h = 0;
			if(j < args.length) {
				for(k = 0;k < 4;j++)
					type[k] = 0;
				
				while(j < args.length) {
					switch(args[j]){
						case "ip":
							types.add(args[j]);
							j++;
							break;
						case "sp":
							types.add(args[j]);
							j++;
							break;
						case "cure":
							types.add(args[j]);
							j++;
							break;
						case "dead":
							types.add(args[j]);
							j++;
							break;
						default:
							h = 1;
							break;
					}
					if(h == 1) 
						break;
				}
			}
			if(n == j)
				return -1;
		}
		return (j - 1);
	}
	
	//����ʡ��
	public int inspectProvince(int j) {
		int k, n = j;
		
		if(j < args.length){
			province[0] = 0; //ȡ��δָ��״̬���
			while(j<args.length) {
				for(k = 0; k < provinceStr.length; k++) {
					if(args[j].equals(provinceStr[k])) { //��������ҵ���Ӧʡ��
						provinces.add(args[j]); //ָ����ʡ����Ҫ���
						j++;
						break;
					}
				}
			}
			provinceIsExist = true;
			provinces = sort();
		}
		if(n == j) //˵��-province������ȷ����
			return -1;
		return (j - 1); //��������Ϊprovince�Ĳ�������Խ��
	}
	private List<String> sort() {
        List<String> list = new ArrayList<String>();
        int size = provinceStr.length;
        for (int i = 0; i < size; i++)
            if (provinces.contains(provinceStr[i]))
                list.add(provinceStr[i]);
        return list;
    }
	
	//�ж������Ƿ�Ϸ�
	public boolean isValidDate(String dateStr) {
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(dateStr);
            String[] sArray = dateStr.split("-");
            for (String s : sArray) {
                boolean isNum = s.matches("[0-9]+");
                if (!isNum)
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
	
	//��ȡ��־�ļ�
	public void execLog() throws Exception{
		File f = new File(logPath);
		String[] logFiles = f.list();
		int l = logFiles.length;
		System.out.println(l);
		List<String> legalFiles = new ArrayList<String>();
		for(int i = 0;i < l;i++) {
			System.out.println(logFiles[i]);
			String suffix = logFiles[i].substring(logFiles[i].lastIndexOf(".") + 1);
			if(suffix.matches("txt"))
				legalFiles.add(logFiles[i]);
		}
		l = legalFiles.size();
		System.out.println(l);
		if(l == 0)
			throw new IllegalException("Error, no legal log file exists in the log directory");

		logFiles = new String[l];
		legalFiles.toArray(logFiles);
		Arrays.sort(logFiles);
		for(int i = 0;i < l;i++) {
			System.out.println(logFiles[i]);
		execFile(logPath + "/" + logFiles[i]);
		}
		int ipSum = 0;
		int spSum = 0;
		int cureSum = 0;
		int deadSum = 0;
		for(Integer i : ip.values())
			ipSum += i;
		ip.put("ȫ��", ipSum);
		for(Integer i : sp.values())
			spSum += i;
		sp.put("ȫ��", spSum);
		for(Integer i : cure.values())
			cureSum += i;
		cure.put("ȫ��", cureSum);
		for(Integer i : dead.values())
			deadSum += i;
		dead.put("ȫ��", deadSum);
		 FileOutputStream outFile = new FileOutputStream(outPath);
	        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outFile, "utf-8"));
	        if (typeIsExist) {
	            if (!provinceIsExist) {
	                // δ�ṩҪ�����ʡ�ݣ��������־�ļ����ṩ��ʡ�ݵ����ݣ�
	                List<String> list = new ArrayList<String>();
	                int size = provinceStr.length;
	                for (int i = 0; i < size; i++) {
	                    // ȫ��0�����������
	                    if (ip.get(provinceStr[i]) == 0 && sp.get(provinceStr[i]) == 0
	                            && cure.get(provinceStr[i]) == 0 && dead.get(provinceStr[i]) == 0)
	                        continue;
	                    else
	                        list.add(provinceStr[i]);
	                }
	                out(writer, list);
	            } else {
	                out(writer, provinces);
	            }
	        } else {
	            if (provinceIsExist) {
	                for (String province : provinces) {
	                    writer.write(province + " ��Ⱦ����" + ip.get(province) + "�� ���ƻ���" + sp.get(province) + "�� ����"
	                            + cure.get(province) + "�� ����" + dead.get(province) + "��\n");
	                }

	            } else {

	                // ����ʡ����������Ӧ״̬�����ڣ�����ǰ�漯������˳������������������Ҳ����˳��
	                Integer[] ipProvincesAmount = new Integer[ip.size()];
	                ip.values().toArray(ipProvincesAmount);
	                Integer[] spProvincesAmount = new Integer[sp.size()];
	                sp.values().toArray(spProvincesAmount);
	                Integer[] cureProvincesAmount = new Integer[cure.size()];
	                cure.values().toArray(cureProvincesAmount);
	                Integer[] deadProvincesAmount = new Integer[dead.size()];
	                dead.values().toArray(deadProvincesAmount);

	                // �����������ļ�(����δ�ṩҪ�����ʡ��ȫ��Ϊ0��������)
	                int size = provinceStr.length;
	                for (int i = 0; i < size; i++) {
	                    if (ipProvincesAmount[i] == 0 && spProvincesAmount[i] == 0 && cureProvincesAmount[i] == 0
	                            && deadProvincesAmount[i] == 0)
	                        continue;
	                    else
	                        writer.write(
	                                provinceStr[i] + " ��Ⱦ����" + ipProvincesAmount[i] + "�� ���ƻ���" + spProvincesAmount[i]
	                                        + "�� ����" + cureProvincesAmount[i] + "�� ����" + deadProvincesAmount[i] + "��\n");
	                }

	            }
	        }
	        writer.write("//���ĵ�������ʵ���ݣ���������ʹ��");
	        writer.close();
	}
	private void out(BufferedWriter writer, List<String> provinces) throws Exception {
        for (String province : provinces) {
            writer.write(province);
            int size = types.size();
            String[] needTypes = new String[size];
            types.toArray(needTypes);
            if (size == 1) {
                if (needTypes[0].equals("ip"))
                    writer.write(" ��Ⱦ����" + ip.get(province) + "��\n");
                else if (needTypes[0].equals("sp"))
                    writer.write(" ���ƻ���" + sp.get(province) + "��\n");
                else if (needTypes[0].equals("cure"))
                    writer.write(" ����" + cure.get(province) + "��\n");
                else
                    writer.write(" ����" + dead.get(province) + "��\n");
                continue;
            }
            if (needTypes[0].equals("ip"))
                writer.write(" ��Ⱦ����" + ip.get(province));
            else if (needTypes[0].equals("sp"))
                writer.write(" ���ƻ���" + sp.get(province));
            else if (needTypes[0].equals("cure"))
                writer.write(" ����" + cure.get(province));
            else
                writer.write(" ����" + dead.get(province));

            for (int i = 1; i < size - 1; i++) {
                if (needTypes[i].equals("ip"))
                    writer.write("�� ��Ⱦ����" + ip.get(province));
                else if (needTypes[i].equals("sp"))
                    writer.write("�� ���ƻ���" + sp.get(province));
                else if (needTypes[i].equals("cure"))
                    writer.write("�� ����" + cure.get(province));
                else
                    writer.write("�� ����" + dead.get(province));
            }
            if (needTypes[size - 1].equals("ip"))
                writer.write("�� ��Ⱦ����" + ip.get(province) + "��\n");
            else if (needTypes[size - 1].equals("sp"))
                writer.write("�� ���ƻ���" + sp.get(province) + "��\n");
            else if (needTypes[size - 1].equals("cure"))
                writer.write("�� ����" + cure.get(province) + "��\n");
            else
                writer.write("�� ����" + dead.get(province) + "��\n");
        }
    }


	
	//������־�ļ�
	public void execFile(String path) throws Exception{
		FileInputStream fs = new FileInputStream(new File(path));
		BufferedReader br = new BufferedReader(new InputStreamReader(fs, "UTF-8"));
		String strLine;
		while((strLine = br.readLine()) != null) {
			if(strLine.matches(s1)) {
				int index = strLine.indexOf(" ���� ��Ⱦ����");
                // ȡ��ʡ�ݣ�ʡ��ǰ������пո�
                String province = strLine.substring(0, index);
                // ȥ��ȫ���ո�
                province.replace(" ", "");

                // ȡ����Ⱦ����
                int sum = getAmount(strLine);
                // �޸�����
                sum += ip.get(province);
                ip.put(province, sum);
				
			}else if (strLine.matches(s2)) {
                int index = strLine.indexOf(" ���� ���ƻ���");
                // ȡ��ʡ�ݣ�ʡ��ǰ������пո�
                String province = strLine.substring(0, index);
                // ȥ��ȫ���ո�
                province.replace(" ", "");

                // ȡ�����ƻ�������
                int sum = getAmount(strLine);
                // �޸�����
                sum += sp.get(province);
                sp.put(province, sum);
            }
			else if (strLine.matches(s3)) {
                // String s3 = "\\s*\\S+ ��Ⱦ���� ���� \\S+ \\d+��\\s*";
                int index = strLine.indexOf(" ��Ⱦ���� ����");
                // ȡ������ʡ�ݣ�ʡ��ǰ������пո�
                String outProvince = strLine.substring(0, index);
                // ȥ��ȫ���ո�
                outProvince.replace(" ", "");

                // ȡ����������
                int sum = getAmount(strLine);
                index = strLine.indexOf(Integer.toString(sum));
                // ȡ������ʡ��
                String inProvince = strLine.substring(strLine.lastIndexOf("����") + 3, index - 1);

                ip.put(outProvince, ip.get(outProvince) - sum);
                ip.put(inProvince, ip.get(inProvince) + sum);
            } else if (strLine.matches(s4)) {
                // String s4 = "\\s*\\S+ ���ƻ��� ���� \\S+ \\d+��\\s*";
                int index = strLine.indexOf(" ���ƻ��� ����");
                // ȡ������ʡ�ݣ�ʡ��ǰ������пո�
                String outProvince = strLine.substring(0, index);
                // ȥ��ȫ���ո�
                outProvince.replace(" ", "");

                // ȡ����������
                int sum = getAmount(strLine);
                index = strLine.indexOf(Integer.toString(sum));
                // ȡ������ʡ��
                String inProvince = strLine.substring(strLine.lastIndexOf("����") + 3, index - 1);

                sp.put(outProvince, sp.get(outProvince) - sum);
                sp.put(inProvince, sp.get(inProvince) + sum);
            } else if (strLine.matches(s5)) {
                int index = strLine.indexOf(" ����");
                // ȡ��ʡ�ݣ�ʡ��ǰ������пո�
                String province = strLine.substring(0, index);
                // ȥ��ȫ���ո�
                province.replace(" ", "");

                // ȡ����������
                int deadSum = getAmount(strLine);
                // ��ø�Ⱦ����
                int ipSum = ip.get(province);
                // ���¸�Ⱦ����
                ip.put(province, ipSum - deadSum);

                deadSum += dead.get(province);
                dead.put(province, deadSum);
            } else if (strLine.matches(s6)) {
                // s6 = "\\s*\\S+ ���� \\d+��\\s*";
                int index = strLine.indexOf(" ����");
                // ȡ��ʡ�ݣ�ʡ��ǰ������пո�
                String province = strLine.substring(0, index);
                // ȥ��ȫ���ո�
                province.replace(" ", "");

                // ȡ����������
                int cureSum = getAmount(strLine);
                // ��ø�Ⱦ����
                int ipSum = ip.get(province);
                // ���¸�Ⱦ����
                ip.put(province, ipSum - cureSum);
                cureSum += cure.get(province);
                cure.put(province, cureSum);

            } else if (strLine.matches(s7)) {
                // String s7 = "\\s*\\S+ ���ƻ��� ȷ���Ⱦ \\d+��\\s*";
                int index = strLine.indexOf(" ���ƻ��� ȷ���Ⱦ");
                // ȡ��ʡ�ݣ�ʡ��ǰ������пո�
                String province = strLine.substring(0, index);
                // ȥ��ȫ���ո�
                province.replace(" ", "");
                int ipSum = getAmount(strLine);
                sp.put(province, sp.get(province) - ipSum);
                ip.put(province, ip.get(province) + ipSum);
                System.out.println(sp);
            } else if (strLine.matches(s8)) {
                // String s8 = "\\s*\\S+ �ų� ���ƻ��� \\d+��\\s*";
                int index = strLine.indexOf(" �ų� ���ƻ���");
                // ȡ��ʡ�ݣ�ʡ��ǰ������пո�
                String province = strLine.substring(0, index);
                // ȥ��ȫ���ո�
                province.replace(" ", "");

                int excludeSum = getAmount(strLine);
                sp.put(province, sp.get(province) - excludeSum);
                System.out.println(sp);
            }
		}
		br.close();
		fs.close();
	}
	public int getAmount(String s) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s);
        m.find();
        return Integer.parseInt(s.substring(m.start(), m.end()));
    }
	
	class IllegalException extends Exception{
		private String message;
		public  IllegalException(String tMessage) {
			message =tMessage;
		}
		public String toString() {
			return message;
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length == 0) {
			System.out.print("No parameters entered.");
			return;
		}
		InfectStatistic s = new InfectStatistic();
		if(s.inspectParameter(args)) {
			try {
				s.execLog();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else 
			return;
	}

}
