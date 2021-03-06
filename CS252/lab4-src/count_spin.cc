
#include <pthread.h>
#include <stdio.h>
#include <unistd.h>
#include <thread.h>

unsigned long lock = 0;
unsigned long m = 0;

unsigned long 
test_and_set(unsigned long * lock)
{
    unsigned long newval = 1;
    asm volatile ("swap [%1],%0"
          :"=r" (newval)
          :"r" (lock), "0" (newval)
          : "memory");
    return newval;
}

void
my_spin_lock( unsigned long * lock )
{
	while (test_and_set(lock) != 0) {
		// give up the CPU		
		thr_yield();
	}
}

void
my_spin_unlock( unsigned long * lock )
{
	*lock = 0;
}

int count;

void increment( int ntimes )
{
	for ( int i = 0; i < ntimes; i++ ) {
		my_spin_lock(&m);
		int c;

		c = count;
		c = c + 1;
		count = c;
		my_spin_unlock(&m);
	}
}

int main( int argc, char ** argv )
{
	int n = 10000000;
	pthread_t t1, t2;
        pthread_attr_t attr;

        pthread_attr_init( &attr );
        pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);

	printf("Start Test. Final count should be %d\n", 2 * n );

	// Create threads
	pthread_create( &t1, &attr, (void * (*)(void *)) increment, 
			(void *) n);

	pthread_create( &t2, &attr, (void * (*)(void *)) increment, 
			(void *) n);

	// Wait until threads are done
	pthread_join( t1, NULL );
	pthread_join( t2, NULL );

	if ( count != 2 * n ) {
		printf("\n****** Error. Final count is %d\n", count );
		printf("****** It should be %d\n", 2 * n );
	}
	else {
		printf("\n>>>>>> O.K. Final count is %d\n", count );
	}
}


