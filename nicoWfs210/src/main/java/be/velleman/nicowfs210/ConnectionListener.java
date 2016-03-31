package be.velleman.nicowfs210;

interface ConnectionListener
{
	public void disconnected();

	public void connected();

	public void newPacketFound(Packet p);
}
