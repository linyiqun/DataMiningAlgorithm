
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * 
 * @author 樊俊彬
 * @Time 2014-1-1
 * @modify mindcont
 * @date 2016-3-26
 * @since 配合 EM 算法进行绘制坐标并打点
 * 
 */
public class DrawPoints extends JFrame {

	private static final long serialVersionUID = 1L;
	private Image iBuffer;

	// 框架起点坐标、宽高
	private final int FRAME_X = 50;
	private final int FRAME_Y = 50;
	private final int FRAME_WIDTH = 500;
	private final int FRAME_HEIGHT = 500;

	// 原点坐标
	private final int Origin_X = FRAME_X + 40;
	private final int Origin_Y = FRAME_Y + FRAME_HEIGHT - 30;

	// X轴、Y轴终点坐标
	private final int XAxis_X = FRAME_X + FRAME_WIDTH - 30;
	private final int XAxis_Y = Origin_Y;
	private final int YAxis_X = Origin_X;
	private final int YAxis_Y = FRAME_Y + 30;
	
	//坐标轴间隔
	private final int INTERVAL = 20;

	
	// 保存Point对象的X Y 坐标
	private int[] Coordinate_X = new int [50];
	private int[] Coordinate_Y = new int [50];
	
   
	public DrawPoints(ArrayList<Point> points) {
		super("EM Demo");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds(300, 100, 600, 650);

		// 添加控制到框架北部区
		JPanel topPanel = new JPanel();
		this.add(topPanel, BorderLayout.NORTH);

		// 文本框
		topPanel.add(new JLabel("EM Demo", JLabel.CENTER));

		//坐标点数据列表中读取 X轴 Y轴的坐标值 分别赋值 给 Coordinate_X Coordinate_Y
		for(int i=0;i<points.size();i++){	
			Point point = points.get(i);
		
			Coordinate_X[i]=point.getX();
			Coordinate_Y[i]=point.getY();
		}
		
		// 添加画布到中央区
	    MyCanvas ChartCanvas = new MyCanvas();
		this.add(ChartCanvas, BorderLayout.CENTER);
		this.setResizable(false);
		this.setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);		
	}


	/**}
	 * 画布绘制坐标系 并打点
	 */
	class MyCanvas extends Canvas {
		
		private static final long serialVersionUID = 1L;
		public void paint(Graphics g) {
			Graphics2D g2D = (Graphics2D) g;

			// 画边框
			g.setColor(Color.BLACK);
			g.draw3DRect(FRAME_X, FRAME_Y, FRAME_WIDTH, FRAME_HEIGHT, true);

			// 画坐标轴
			g.setColor(Color.BLACK);
			g2D.setStroke(new BasicStroke(Float.parseFloat("2.0f")));
			
			// X轴及方向箭头
			g.drawLine(Origin_X, Origin_Y, XAxis_X, XAxis_Y);
			g.drawLine(XAxis_X, XAxis_Y, XAxis_X - 5, XAxis_Y - 5);
			g.drawLine(XAxis_X, XAxis_Y, XAxis_X - 5, XAxis_Y + 5);
			
			// Y轴及方向箭头
			g.drawLine(Origin_X, Origin_Y, YAxis_X, YAxis_Y);
			g.drawLine(YAxis_X, YAxis_Y, YAxis_X - 5, YAxis_Y + 5);
			g.drawLine(YAxis_X, YAxis_Y, YAxis_X + 5, YAxis_Y + 5);

			// 画X轴上刻度
			g.setColor(Color.BLUE);
			g2D.setStroke(new BasicStroke(Float.parseFloat("1.0f")));
			for (int i = Origin_X + 15, j = 0; i < XAxis_X; i += INTERVAL, j += 20) {
				g.drawString(j + "", i - 20, Origin_Y + 20);

			}
			g.drawString("X轴", XAxis_X + 5, XAxis_Y + 5);

			// 画Y轴上刻度
			for (int i = Origin_Y, j = 0; i > YAxis_Y; i -= INTERVAL, j += 20) {
				g.drawString(j + "", Origin_X - 20, i + 3);
			}
			g.drawString("Y轴", YAxis_X - 5, YAxis_Y - 5);

			// 画网格线
			g.setColor(Color.BLACK);
			// 横线
			for (int i = Origin_Y - INTERVAL; i > YAxis_Y; i -= INTERVAL) {
				g.drawLine(Origin_X, i, Origin_X + 21 * INTERVAL, i);
			}
			// 竖线
			for (int i = Origin_X + INTERVAL; i < XAxis_X; i += INTERVAL) {
				g.drawLine(i, Origin_Y, i, Origin_Y - 21 * INTERVAL);

			}

			//设置画笔颜色为绿色
			g.setColor(Color.green);
			g2D.setStroke(new BasicStroke(Float.parseFloat("5.0f")));
			//画出 簇点
			g.drawOval(Origin_X+Coordinate_X[0], Origin_Y -Coordinate_Y[0], 5, 5);
			g.drawOval(Origin_X+Coordinate_X[1], Origin_Y -Coordinate_Y[1], 5, 5);
			
			//设置画笔颜色 为红色  
			g.setColor(Color.red);
			g2D.setStroke(new BasicStroke(Float.parseFloat("5.0f")));
			//画其余各点
			for (int i = 2; i < Coordinate_X.length ;i++) {
				g.drawLine(Origin_X+ Coordinate_X[i], 
						Origin_Y - Coordinate_Y[i], 
						Origin_X+ Coordinate_X[i],
						Origin_Y - Coordinate_Y[i]);
			}
			

		}

		// 双缓冲技术解决图像显示问题
		public void update(Graphics g) {
			if (iBuffer == null) {
				iBuffer = createImage(this.getSize().width,
						this.getSize().height);

			}
			Graphics gBuffer = iBuffer.getGraphics();
			gBuffer.setColor(getBackground());
			gBuffer.fillRect(0, 0, this.getSize().width, this.getSize().height);
			paint(gBuffer);
			gBuffer.dispose();
			g.drawImage(iBuffer, 0, 0, this);
		}
	}
	

}

