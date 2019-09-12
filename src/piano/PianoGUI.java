package piano;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PianoGUI extends JFrame
{
	public static final int WINDOW_WIDTH = 2000;
	private Color clientColor; // sent by server - randomly generated int
	private ObjectOutputStream out;
	private MidiChannel channel;
	private JPanel contents;

	public PianoGUI() throws MidiUnavailableException
	{
		this.setTitle("MY PIANO");
		this.setSize(WINDOW_WIDTH, 800);
		this.setMinimumSize(new Dimension(850, 800));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		contents = new JPanel();
		contents.setLayout(new BorderLayout());

		JPanel top = new JPanel();
		//top.setSize(this.getWidth(), this.getHeight() / 2);
		top.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		contents.add(top, BorderLayout.NORTH);
		PianoLabel[] topRowLabels = new PianoLabel[KeyStats.NUM_KEYS];
		initializeTopLabels(top, topRowLabels);

		JPanel bottom = new JPanel();
		//bottom.setSize(this.getWidth(),this.getHeight() / 2);
		bottom.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		contents.add(bottom, BorderLayout.CENTER);
		PianoLabel[] bottomRowLabels = new PianoLabel[13];
		initializeBottomLabels(bottom, bottomRowLabels);

		this.add(contents, BorderLayout.CENTER);
		ArrayList<Key> keys = new ArrayList<>();
		linkLabelsToKeys(topRowLabels, bottomRowLabels, keys);

		pack();
		this.setLocationRelativeTo(null);

		// setting up sound
		Synthesizer synth = MidiSystem.getSynthesizer();
		synth.open();
		channel = synth.getChannels()[SoundSettings.CHANNEL];

		// outStream remains null until a connection is made
		ClientReceiver conn = new ClientReceiver(this, keys);
		conn.start();
	}

	private void linkLabelsToKeys(PianoLabel[] topRowLabels, PianoLabel[] bottomRowLabels, ArrayList<Key> keys)
	{
		Key k;
		for (int i = 0; i < KeyStats.NUM_KEYS; i++)
		{
			if ((i & 1) == 0) // white key
			{
				k = new Key(new PianoLabel[] { topRowLabels[i], bottomRowLabels[i] }, keys.size(), this);
				topRowLabels[i].setKey(k);
				bottomRowLabels[i].setKey(k);
				keys.add(k);
			}
			else
			// black key
			{
				if (i != 5) // not skinny dude
				{
					k = new Key(new PianoLabel[] { topRowLabels[i] }, keys.size(), this);
					topRowLabels[i].setKey(k);
					keys.add(k);
				}
			}
		}
	}

	private void initializeBottomLabels(JPanel bottom, PianoLabel[] bottomRowLabels)
	{
		int bottomWidthUnit = this.getWidth() / WINDOW_WIDTH; //bottom width unit measure
		int bottomHeight = this.getHeight() / 2;
		for (int i = 0; i < KeyStats.NUM_KEYS; i++)
		{
			if ((i & 1) == 0) // whiteKey
			{
				bottomRowLabels[i] = new PianoLabel(new Dimension(i == 8 || i == 10
						? (bottomWidthUnit * KeyStats.BOTTOM_FAT_WIDTH) : (bottomWidthUnit * KeyStats.BOTTOM_WHITE_WIDTH),
						bottomHeight),
						Color.WHITE);
				bottomRowLabels[i].addMouseListener(new KeyListener());
			}
			else //blackKey
			{
				bottomRowLabels[i] = new PianoLabel(new Dimension(bottomWidthUnit * KeyStats.BOTTOM_SKINNY_WIDTH, bottomHeight), Color.BLACK);
			}
			bottom.add(bottomRowLabels[i]);
		}
	}

	private void initializeTopLabels(JPanel top, PianoLabel[] topRowLabels)
	{
		int topWidthUnit = this.getWidth() / WINDOW_WIDTH; //bottom width unit measure
		int topHeight = this.getHeight() / 2;
		for (int i = 0; i < KeyStats.NUM_KEYS; i++)
		{
			if ((i & 1) == 0) // white
			{
				topRowLabels[i] = new PianoLabel(new Dimension(topWidthUnit * KeyStats.TOP_WHITE_WIDTH, topHeight), Color.WHITE);
			}
			else if (i == 5) // skinny dude
			{
				topRowLabels[i] = new PianoLabel(new Dimension(topWidthUnit * KeyStats.TOP_SKINNY_WIDTH, topHeight), Color.BLACK);
			}
			else
			// black
			{
				topRowLabels[i] = new PianoLabel(new Dimension(topWidthUnit * KeyStats.TOP_BLACK_WIDTH, topHeight), Color.BLACK);
			}

			topRowLabels[i].addMouseListener(new KeyListener());
			top.add(topRowLabels[i]);
		}
	}

	public Color getClientColor()
	{
		return clientColor;
	}

	public void setClientColor(Color clientColor)
	{
		this.clientColor = clientColor;
	}

	public static void main(String[] args)
	{
		PianoGUI gui;
		try
		{
			gui = new PianoGUI();
			gui.setVisible(true);
			gui.playIntro();
		}
		catch (MidiUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	private void playIntro()
	{
		try
		{
			int[] notes = { Notes.C, Notes.D, Notes.E, Notes.F, Notes.G, Notes.A, Notes.B };
			for (int i = 0; i < notes.length; ++i)
			{
				channel.noteOn(notes[i], SoundSettings.VOLUME);
				Thread.sleep(100);
				channel.noteOff(notes[i]);
			}
			// Play a C major chord.
			channel.noteOn(Notes.C, SoundSettings.VOLUME);
			channel.noteOn(Notes.E, SoundSettings.VOLUME);
			channel.noteOn(Notes.G, SoundSettings.VOLUME);
			Thread.sleep(3000);
			channel.allNotesOff();
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void setObjectOutputStream(ObjectOutputStream objectOutputStream)
	{
		out = objectOutputStream;
	}

	public ObjectOutputStream getObjectOutputStream()
	{
		return out;
	}

	public MidiChannel getChannel()
	{
		return channel;
	}
}
