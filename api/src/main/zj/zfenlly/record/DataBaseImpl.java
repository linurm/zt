package zj.zfenlly.record;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.zfenlly.msc.DaoMaster;
import com.zfenlly.msc.DaoSession;
import com.zfenlly.msc.MSC;
import com.zfenlly.msc.MSCDao;
import com.zfenlly.wb.WB;
import com.zfenlly.wb.WBDao;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2016/7/18.
 */
public class DataBaseImpl {

    public static class MSCDataBaseOp {


        public void insert(Context mContext, MSC mMsc) {
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            MSCDao mscDao;
            final String DATABASE_PATH = Environment.getExternalStorageDirectory()
                    + "/" + "xxx/";
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH + "msc-db", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            mscDao = daoSession.getMSCDao();
            mscDao.insert(mMsc);
        }

        public MSC getCurrMSC(Context mContext) {
            SQLiteDatabase db;
            DaoMaster daoMaster;
            DaoSession daoSession;
            MSCDao mscDao;
            final String DATABASE_PATH = Environment.getExternalStorageDirectory()
                    + "/" + "xxx/";
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH + "msc-db", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            mscDao = daoSession.getMSCDao();
            QueryBuilder<MSC> qb = mscDao.queryBuilder();
            MSC mMSC = null;
            if (qb.list().size() >= 1)
                mMSC = qb.list().get(qb.list().size() - 1);
            return mMSC;
        }

    }

    public static class WBDataBaseOp {
        public void insert(Context mContext, WB mWb) {
            SQLiteDatabase db;
            com.zfenlly.wb.DaoMaster daoMaster;
            com.zfenlly.wb.DaoSession daoSession;
            WBDao wbDao;
            final String DATABASE_PATH = Environment.getExternalStorageDirectory()
                    + "/" + "xxx/";
            com.zfenlly.wb.DaoMaster.DevOpenHelper helper = new com.zfenlly.wb.DaoMaster.DevOpenHelper(mContext, DATABASE_PATH + "wb-db", null);
            db = helper.getWritableDatabase();
            daoMaster = new com.zfenlly.wb.DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWBDao();
            wbDao.insert(mWb);
        }

        public WB getCurrWB(Context mContext) {
            SQLiteDatabase db;
            com.zfenlly.wb.DaoMaster daoMaster;
            com.zfenlly.wb.DaoSession daoSession;
            WBDao wbDao;
            final String DATABASE_PATH = Environment.getExternalStorageDirectory()
                    + "/" + "xxx/";
            com.zfenlly.wb.DaoMaster.DevOpenHelper helper = new com.zfenlly.wb.DaoMaster.DevOpenHelper(mContext, DATABASE_PATH + "wb-db", null);
            db = helper.getWritableDatabase();
            daoMaster = new com.zfenlly.wb.DaoMaster(db);
            daoSession = daoMaster.newSession();
            wbDao = daoSession.getWBDao();
            QueryBuilder<WB> qb = wbDao.queryBuilder();
            WB mWB = null;
            if (qb.list().size() >= 1)
                mWB = qb.list().get(qb.list().size() - 1);
            return mWB;
        }
    }
}
