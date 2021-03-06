/* send.c - send */

#include <xinu.h>

/*------------------------------------------------------------------------
 *  send  -  pass a message to a process and start recipient if waiting
 *------------------------------------------------------------------------
 */
syscall	sendq(
	  pid32		pid,		/* ID of recipient process	*/
	  umsg32	msg		/* contents of message		*/
	)
{
	intmask	mask;			/* saved interrupt mask		*/
	struct	procent *prptr;		/* ptr to process' table entry	*/

	mask = disable();
	if (isbadpid(pid)) {
		restore(mask);
		return SYSERR;
	}

	prptr = &proctab[pid];
	if (prptr->prstate == PR_FREE) {
		restore(mask);
		return SYSERR;
	}

	if (prptr->nummessages < MSGQ_SIZE) {
		messageEnqueue(msg, pid);
	}	
	else if (prptr->prhasmsg) {
		struct	procent *sender;
		sender = &proctab[currpid];

		sender->prstate = PR_SND;
		sender->sndmsg = msg;
		sender->sndflag = TRUE;

		enqueue(currpid, prptr->senderqueue);

		kprintf("Process %d is getting blocked...\n\r", currpid);
		resched();
		kprintf("Process %d is unblocked.\n\r", currpid);
	}

	prptr->prhasmsg = TRUE;		/* indicate message is waiting	*/

	/* If recipient waiting or in timed-wait make it ready */

	if (prptr->prstate == PR_RECV) {
		ready(pid, RESCHED_YES);
	} else if (prptr->prstate == PR_RECTIM) {
		unsleep(pid);
		ready(pid, RESCHED_YES);
	}

	restore(mask);		/* restore interrupts */
	return OK;
}
