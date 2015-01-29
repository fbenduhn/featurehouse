
void funcdecl(int i, double t, struct t test);

void funcdecl(i, t, test)
	int i;
	double t;
	char test;{

}

int
__db_master_open(subdbp, ip, txn, name, flags, mode, dbpp)
	DB *subdbp;
	DB_THREAD_INFO *ip;
	DB_TXN *txn;
	const char *name;
	u_int32_t flags;
	int mode;
	DB **dbpp;
{
	DB *dbp;
	int ret;

	*dbpp = NULL;

	/* Open up a handle on the main database. */
	if ((ret = __db_create_internal(&dbp, subdbp->env, 0)) != 0)
		return (ret);

	/*
	 * It's always a btree.
	 * Run in the transaction we've created.
	 * Set the pagesize in case we're creating a new database.
	 * Flag that we're creating a database with subdatabases.
	 */
	dbp->pgsize = subdbp->pgsize;
	F_SET(dbp, DB_AM_SUBDB);
	F_SET(dbp, F_ISSET(subdbp,
	    DB_AM_RECOVER | DB_AM_SWAP |
	    DB_AM_ENCRYPT | DB_AM_CHKSUM | DB_AM_NOT_DURABLE));

	/*
	 * If there was a subdb specified, then we only want to apply
	 * DB_EXCL to the subdb, not the actual file.  We only got here
	 * because there was a subdb specified.
	 */
	LF_CLR(DB_EXCL);
	LF_SET(DB_RDWRMASTER);
	if ((ret = __db_open(dbp, ip, txn,
	    name, NULL, DB_BTREE, flags, mode, PGNO_BASE_MD)) != 0)
		goto err;

	/*
	 * The items in dbp are initialized from the master file's meta page.
	 * Other items such as checksum and encryption are checked when we
	 * read the meta-page, so we do not check those here.  However, if
	 * the meta-page caused checksumming to be turned on and it wasn't
	 * already, set it here.
	 */
	if (F_ISSET(dbp, DB_AM_CHKSUM))
		F_SET(subdbp, DB_AM_CHKSUM);

	/*
	 * The user may have specified a page size for an existing file,
	 * which we want to ignore.
	 */
	subdbp->pgsize = dbp->pgsize;
	*dbpp = dbp;

	if (0) {
err:		if (!F_ISSET(dbp, DB_AM_DISCARD))
			(void)__db_close(dbp, txn, DB_NOSYNC);
	}

	return (ret);
}