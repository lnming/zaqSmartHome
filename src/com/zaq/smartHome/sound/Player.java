package com.zaq.smartHome.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

import com.zaq.smartHome.exception.SystemException;

/**
 * 音频播放
 * @author zaqzaq
 * 2015年12月11日
 *
 */
public class Player {
	private static Logger logger = Logger.getLogger(Player.class);
	private Player(){}
	//播音设备
	private static SourceDataLine sd =null;
	// 定义存放录音的字节数组,作为缓冲区
	private static byte bts[] = new byte[10000];
	/**
	 * 播放音频
	 * @param audioData
	 * @throws LineUnavailableException
	 * @throws SystemException
	 */
	public static synchronized void play(InputStream audioIs) throws LineUnavailableException, SystemException{
		audioIs=new BufferedInputStream(audioIs);
		AudioInputStream ais=null;
		try {
			  // 获取音频输入流
			  ais = AudioSystem .getAudioInputStream(audioIs);
			  // 获取音频编码对象
			  AudioFormat audioFormat = ais.getFormat();
			//打开播音设备
				openSD(audioFormat);
				logger.debug("开始播放音频");
				int cnt;
	            //读取数据到缓存数据
	            while ((cnt = ais.read(bts, 0, bts.length)) != -1) 
	            {
	                if (cnt > 0) 
	                {
	                    //写入缓存数据
	                    //将音频数据写入到混频器
	                    sd.write(bts, 0, cnt);
	                }
	            }
            
		} catch (Exception e) {
			throw new SystemException("播音失败", e);
		}finally{
			try {
				//关闭流
				if(ais != null)
				{
					ais.close();
				}
				if(audioIs != null)
				{
					audioIs.close();
				}
			} catch (Exception e) {		
			}
//			 sd.drain();
	         sd.close();
		}
		
		logger.debug("音频播放完毕");
	}
	
	public static void play(File audioFile) throws LineUnavailableException, SystemException, IOException {
		play(new FileInputStream(audioFile));
	}
	
	//打开播音设备
	private static void openSD(AudioFormat af) throws LineUnavailableException {
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, af);
		sd = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		sd.open(af);
		sd.start();
	}
	
	public static void main(String[] args) throws LineUnavailableException, SystemException, IOException {
		play(new File(AudioUtil.TMP_RECORD));
		play(new File(AudioUtil.TMP_RECORD));
	}
}
