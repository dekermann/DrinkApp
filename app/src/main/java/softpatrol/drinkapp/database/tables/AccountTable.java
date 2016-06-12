package softpatrol.drinkapp.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import softpatrol.drinkapp.database.models.account.Account;

/**
 * Divine was here on 2016-06-09!
 * Copy this code for instant regret...
 */
public class AccountTable {

    private static final String TABLE_ACCOUNTS = "accounts";

    private static final String ACCOUNT_ID = "id";
    private static final String ACCOUNT_TOKEN = "token";
    private static final String ACCOUNT_ALIAS = "alias";

    private static final String DATABASE_CREATE = "create table " + TABLE_ACCOUNTS + "(" +
            ACCOUNT_ID + " integer primary key autoincrement, " +
            ACCOUNT_TOKEN + " text not null, " +
            ACCOUNT_ALIAS + " text not null);";

    private static String[] allColumns = {ACCOUNT_ID,
            ACCOUNT_TOKEN, ACCOUNT_ALIAS};

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
    }

    public static void createAccount(SQLiteDatabase db, Account account) {
        ContentValues values = new ContentValues();

        values.put(ACCOUNT_TOKEN, account.getToken());
        values.put(ACCOUNT_ALIAS, account.getAlias());

        db.insert(TABLE_ACCOUNTS, null, values);
        db.close();
    }

    public static Account getAccount(SQLiteDatabase db, long id) {
        if (id == -1) return null;
        Cursor cursor = db.query(TABLE_ACCOUNTS, allColumns, ACCOUNT_ID + " = '" + id + "'", null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();

        Account account = cursorToAccount(cursor);

        cursor.close();
        return account;
    }

    public static Account getAccount(SQLiteDatabase db, String alias) {
        Cursor cursor = db.query(TABLE_ACCOUNTS, allColumns, ACCOUNT_ALIAS + " = '" + alias + "'", null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();

        Account account = cursorToAccount(cursor);

        cursor.close();
        return account;
    }

    public static List<Account> getAllAccounts(SQLiteDatabase db) {
        List<Account> accounts = new ArrayList<>();

        Cursor cursor = db.query(TABLE_ACCOUNTS, allColumns, null, null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Account account = cursorToAccount(cursor);
            accounts.add(account);
            cursor.moveToNext();
        }

        cursor.close();
        return accounts;
    }

    public static int updateAccount(SQLiteDatabase db, Account account) {
        ContentValues values = new ContentValues();

        values.put(ACCOUNT_ID, account.getId());
        values.put(ACCOUNT_TOKEN, account.getToken());
        values.put(ACCOUNT_ALIAS, account.getAlias());

        int ret = db.update(TABLE_ACCOUNTS, values, ACCOUNT_ID + " = '" + account.getId() + "'", null);
        db.close();
        return ret;
    }

    private static Account cursorToAccount(Cursor cursor) {
        Account account = new Account();
        account.setId(cursor.getLong(0));
        account.setToken(cursor.getString(1));
        account.setAlias(cursor.getString(2));
        return account;
    }

}
