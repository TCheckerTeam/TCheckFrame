package TCheckServer.Engine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import tmax.webt.WebtBuffer;
import tmax.webt.WebtConnection;
import tmax.webt.WebtConnectionPool;
import tmax.webt.WebtRemoteService;
import tmax.webt.WebtServiceException;
import anylink.common.object.AnyLinkHeader;

public class WebtSender {
	int AL_MSG_DATA = 101;
	int REQ_EXTERNAL = 1;
	int REQ_INTERNAL = 11;
 
	public byte[] MapperCall(String OoB, byte[] data, String serviceName) throws WebtServiceException {

		WebtConnection con = null;
		WebtBuffer sndbuf = null;
		WebtBuffer rcvbuf = null;
		
		try {
			con = WebtConnectionPool.getConnection(OoB);

		} catch (Exception e) {
			throw new WebtServiceException(330, "");
		}

		try {
			WebtRemoteService service = new WebtRemoteService(serviceName, con);
			sndbuf = service.createCarrayBuffer(data.length);
			sndbuf = service.createCarrayBuffer();
			sndbuf.setBytes(data);
			rcvbuf = service.tpcall(sndbuf);
			System.out.println("Recv Data : " + rcvbuf);

		} catch (WebtServiceException wse) {
			int errno = wse.getTPError();
			wse.printStackTrace();
			switch(errno){
				case 107:
					throw new WebtServiceException(errno,"invalid inst_code");
				case 108:
					throw new WebtServiceException(errno,"invalid appl_code");
				case 109:
					throw new WebtServiceException(errno,"invalid kind_code");
				case 110:
					throw new WebtServiceException(errno,"invalid tx_code");
				case 131:
					throw new WebtServiceException(errno,"anylink header stream is too short");
				default:
					throw new WebtServiceException(errno,wse.getTPErrorMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebtServiceException(999,"Etc error");
		} finally {
			con.close();
		}
		return rcvbuf.getBytes();
	}
 
	public byte[] MapperCall(String ip, int port, byte[] data, String serviceName) throws WebtServiceException {

		WebtConnection con = null;
		WebtBuffer sndbuf = null;
		WebtBuffer rcvbuf = null;
		
		try {
			con = new WebtConnection(ip, port);
		} catch (Exception e) {
			throw new WebtServiceException(330, "");
		}

		try {
			WebtRemoteService service = new WebtRemoteService(serviceName, con);
			sndbuf = service.createCarrayBuffer(data.length);
			sndbuf = service.createCarrayBuffer();
			sndbuf.setBytes(data);
			rcvbuf = service.tpcall(sndbuf);
		} catch (WebtServiceException wse) {
			int errno = wse.getTPError();
			//System.out.println(errno);
			wse.printStackTrace();
			switch(errno){
				case 107:
					throw new WebtServiceException(errno,"invalid inst_code");
				case 108:
					throw new WebtServiceException(errno,"invalid appl_code");
				case 109:
					throw new WebtServiceException(errno,"invalid kind_code");
				case 110:
					throw new WebtServiceException(errno,"invalid tx_code");
				case 131:
					throw new WebtServiceException(errno,"anylink header stream is too short");
				default:
					throw new WebtServiceException(errno,wse.getTPErrorMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebtServiceException(999,"Etc error");
		} finally {
			con.close();
		}
		return rcvbuf.getBytes();
	}

	public int MapperACall(String OoB, byte[] data, String serviceName) throws WebtServiceException {

		WebtConnection con = null;
		WebtBuffer sndbuf = null;
		int returnInt;
 
		try {
			con = WebtConnectionPool.getConnection(OoB);
		} catch (Exception e) {
			throw new WebtServiceException(330,"WebT connection is null");
		}

		try {
			WebtRemoteService service = new WebtRemoteService(serviceName, con);
			sndbuf = service.createCarrayBuffer();
			sndbuf.setBytes(data);
			returnInt = service.tpacall(sndbuf);

		} catch (WebtServiceException wse) {
			int errno = wse.getTPError();
			wse.printStackTrace();
			switch(errno){
				case 107:
					throw new WebtServiceException(errno,"invalid inst_code");
				case 108:
					throw new WebtServiceException(errno,"invalid appl_code");
				case 109:
					throw new WebtServiceException(errno,"invalid kind_code");
				case 110:
					throw new WebtServiceException(errno,"invalid tx_code");
				case 131:
					throw new WebtServiceException(errno,"anylink header stream is too short");
				default:
					throw new WebtServiceException(errno,wse.getTPErrorMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebtServiceException(999,"Etc error");
		} finally {
			con.close();
		}
		return returnInt;
	}
 
	public byte[] makeHeader(String instCode, String applCode, String kindCode, String txCode, byte[] data) 
	{
		byte[] returnByte = null;
		AnyLinkHeader ah = new AnyLinkHeader();
		ByteArrayOutputStream outStrm = new ByteArrayOutputStream();
		
		ah.setMagic(5555);
		ah.setSeqno(0);
		ah.setReqtype(REQ_EXTERNAL);
		ah.setMsgtype(AL_MSG_DATA);
		ah.setFlags(0);
		ah.setEtc(0);
		ah.setEtc2(0);
		ah.setInst_code(instCode);
		ah.setAppl_code(applCode);
		ah.setKind_code(kindCode);
		ah.setTx_code(txCode);
 
		try {
			outStrm.write(ah.getBytes());
			outStrm.write(data);
			outStrm.flush();
			returnByte = outStrm.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(outStrm!=null){
				try {
					outStrm.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
 
		return returnByte;

	}

	public String addSpace(String arg, int size, int totSize) {
		if (arg.length() == totSize) return arg;
		while (arg.length() < totSize)  arg += " ";
	 
		byte[] tmp = arg.getBytes();
		tmp[size] = 0x00;
		arg = new String(tmp);
		return arg;
	}

	public byte[] htonl(int arg) {
		byte[] bigEndian = new byte[4];
		bigEndian[0] = (byte)((arg & 0xFF000000) >> 24);
		bigEndian[1] = (byte)((arg & 0x00FF0000) >> 16);
		bigEndian[2] = (byte)((arg & 0x0000FF00) >> 8);
		bigEndian[3] = (byte)(arg & 0x000000FF);
		return bigEndian;
	}
}
