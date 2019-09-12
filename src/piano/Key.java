package piano;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.Timer;

public class Key
{
	private PianoLabel[] pianoLabels;
	private static final Color[] colors =
			{new Color(204, 0, 0), new Color(255, 65, 0),new Color(255, 170, 0),new Color(255, 255, 0),
					new Color(102, 255, 51), new Color(0, 153, 51),new Color(0, 204, 153),new Color(0, 153, 204),
					new Color(0, 0, 255), new Color(153, 51, 255),new Color(153, 51, 153),new Color(204, 102, 153)};
	private int position;
	private PianoGUI gui;

	public Key(PianoLabel[] pianoLabels, int position, PianoGUI gui)
	{
		this.pianoLabels = pianoLabels;
		this.position = position;
		this.gui = gui;
	}

	public void setColor(Color color)
	{
		for (PianoLabel pl : pianoLabels)
		{
			pl.setBackground(color);
			pl.repaint();
		}
	}

	public void play(Color color)
	{
		// change background for as as long as note plays
		SoundThread s = new SoundThread(position + Notes.C, gui.getChannel()); // pass in pitch to play
		s.start();
		setColor(colors[position]);
		ActionListener action = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				resetColor();
			}
		};

		Timer t = new Timer(1000, action);
		t.start();
	}

	public void sendPacket()
	{
		ObjectOutputStream oos;
		try
		{
			oos = gui.getObjectOutputStream();
			oos.writeObject(new PianoPacket(position, gui.getClientColor()));
			oos.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void resetColor()
	{
		for (PianoLabel pl : pianoLabels)
		{
			pl.setBackground(pl.getPreferredColor());
			pl.repaint();
		}
	}

	public int getPosition()
	{
		return position;
	}

}
