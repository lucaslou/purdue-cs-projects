#include <xinu.h>

syscall pipwrite(int32 pip, char *buf, uint32 len) {
	intmask mask;
	struct pipe *pipeptr;

	mask = disable();

	if (isbadpipeid(pip) || isbadpid(getpid()) || (pipeptr = &pipelist[pip])->pipestate != PIPE_CONNECTED || (pipeptr = &pipelist[pip])->end2 != getpid()) {
		if (isbadpid(getpid()))
		{
			kprintf("PIPWRITE: Owner had bad PID!\n\r");
		}
		
		restore(mask);
		return(SYSERR);
	}

	pipeptr = &pipelist[pip];
	int i = 0;  // keep track of length
	int writePos = pipeptr->writePos;
	int readPos = pipeptr->readPos;

	int orientation = writePos - readPos;
	int available;
	
	if (orientation > 0) 
	{
		available = PIPE_SIZE - writePos + readPos;
	}
	else if (orientation < 0)
	{
		available = readPos - writePos;
	}
	else 
	{
		available = PIPE_SIZE - writePos;
	}
	
	
	if (available <= 0) 
	{
		for (i = 0; i < len; i++)
		{
			wait(pipeptr->psem);
				if (pipeptr->pipestate != PIPE_CONNECTED || isbadpid(pipeptr->ownerPID)) {
					return SYSERR;
				}
				
				pipeptr->buffer[writePos] = buf[i];
				writePos++; 
				
				if (pipeptr->writePos == PIPE_SIZE) pipeptr->writePos = 0;
				else pipeptr->writePos = writePos;
	
			signal(pipeptr->csem);
		}

		restore(mask);
		return(len);
	}
	else if (available <= len)
	{
		for (i = 0; i < available; i++)
		{

			wait(pipeptr->psem);
				if (pipeptr->pipestate != PIPE_CONNECTED || isbadpid(pipeptr->ownerPID)) {
					return SYSERR;
				}
				
				pipeptr->buffer[writePos] = buf[i];
				writePos++; 
				
				if (pipeptr->writePos == PIPE_SIZE) pipeptr->writePos = 0;
				else pipeptr->writePos = writePos;
			signal(pipeptr->csem);
		}

		restore(mask);
		return(available);
	}
	else 
	{
		for (i = 0; i < len; i++)
		{
			wait(pipeptr->psem);
				if (pipeptr->pipestate != PIPE_CONNECTED || isbadpid(pipeptr->ownerPID)) {
					return SYSERR;
				}
				
				pipeptr->buffer[writePos] = buf[i];
				writePos++; 
			
				if (pipeptr->writePos == PIPE_SIZE) pipeptr->writePos = 0;
				else pipeptr->writePos = writePos;
			signal(pipeptr->csem);
		}

		restore(mask);
		return(len);
	}
}
