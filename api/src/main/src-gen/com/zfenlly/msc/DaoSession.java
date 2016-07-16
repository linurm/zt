package com.zfenlly.msc;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.zfenlly.msc.MSC;

import com.zfenlly.msc.MSCDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig mSCDaoConfig;

    private final MSCDao mSCDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        mSCDaoConfig = daoConfigMap.get(MSCDao.class).clone();
        mSCDaoConfig.initIdentityScope(type);

        mSCDao = new MSCDao(mSCDaoConfig, this);

        registerDao(MSC.class, mSCDao);
    }
    
    public void clear() {
        mSCDaoConfig.getIdentityScope().clear();
    }

    public MSCDao getMSCDao() {
        return mSCDao;
    }

}
