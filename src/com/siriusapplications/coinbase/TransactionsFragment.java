package com.siriusapplications.coinbase;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.siriusapplications.coinbase.api.LoginManager;
import com.siriusapplications.coinbase.api.RpcManager;

public class TransactionsFragment extends ListFragment {

  private class LoadBalanceTask extends AsyncTask<Void, Void, String[]> {

    @Override
    protected String[] doInBackground(Void... params) {

      try {

        JSONObject balance = RpcManager.getInstance().callGet(getActivity(), "account/balance");
        
        return new String[] { balance.getString("amount"), balance.getString("currency") };

      } catch (IOException e) {

        e.printStackTrace();
      } catch (JSONException e) {

        e.printStackTrace();
      }

      return null;
    }

    @Override
    protected void onPreExecute() {

      mBalanceText.setTextColor(getResources().getColor(R.color.wallet_balance_color_invalid));
    }

    @Override
    protected void onPostExecute(String[] result) {

      if(result == null) {
        mBalanceText.setText(null);
        mBalanceText.setTextColor(getResources().getColor(R.color.wallet_balance_color_invalid));

        Toast.makeText(getActivity(), R.string.wallet_balance_error, Toast.LENGTH_SHORT).show();
      } else {

        BigDecimal balance = new BigDecimal(result[0]);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(4);
        df.setMinimumFractionDigits(4);
        df.setGroupingUsed(false);
        String balanceString = df.format(balance);

        mBalanceText.setTextColor(getResources().getColor(R.color.wallet_balance_color));
        mBalanceText.setText(String.format(getActivity().getString(R.string.wallet_balance), balanceString));
        mBalanceCurrency.setText(String.format(getActivity().getString(R.string.wallet_balance_currency), result[1]));
        
      }
    }

  }
  ListView mListView;
  TextView mBalanceText, mBalanceCurrency, mAccount;

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    // Inflate base layout
    ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_transactions, container, false);

    mListView = (ListView) view.findViewById(android.R.id.list);

    // Inflate header (which contains account balance)
    View headerView = inflater.inflate(R.layout.fragment_transactions_header, null, false);
    view.addView(headerView, 0);

    mBalanceText = (TextView) headerView.findViewById(R.id.wallet_balance);
    mBalanceCurrency = (TextView) headerView.findViewById(R.id.wallet_balance_currency);
    mAccount = (TextView) headerView.findViewById(R.id.wallet_account);

    mAccount.setText(LoginManager.getInstance().getSelectedAccountName(getActivity()));
    
    return view;
  }

  @Override
  public void onResume() {

    super.onResume();

    // Reload balance
    new LoadBalanceTask().execute();
  }

}
