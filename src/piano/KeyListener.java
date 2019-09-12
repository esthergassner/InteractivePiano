package piano;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class KeyListener implements MouseListener
{
	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{

	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{

	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		PianoLabel pl = (PianoLabel) arg0.getSource();
		pl.getKey().sendPacket();
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{

	}
}
