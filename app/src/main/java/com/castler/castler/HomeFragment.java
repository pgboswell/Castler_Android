package com.castler.castler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, ListView.OnItemClickListener  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View thisView = null;

    private OnFragmentInteractionListener mListener;
    Button btnRecordNewGame = null;
    private int removeButtonClicked = 0;

    private ArrayList<Game> recordedGameList = new ArrayList<Game>();
    private ListView listViewRecordedGames = null;
    private GameListArrayAdapter glaa = null;
    //private Button btnEditGame = null;
    //private Button btnRemoveGame = null;

    AlertDialog endDialog = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (thisView == null) {
            // Inflate the layout for this fragment
            thisView = inflater.inflate(R.layout.fragment_home, container, false);

            btnRecordNewGame = (Button) thisView.findViewById(R.id.btn_record_new_game);
            btnRecordNewGame.setOnClickListener(this);

            /*btnEditGame = (Button)thisView.findViewById(R.id.button_edit_game);
            btnEditGame.setOnClickListener(this);

            btnRemoveGame = (Button)thisView.findViewById(R.id.button_delete_game);
            btnRemoveGame.setOnClickListener(this);*/

            thisView.setFocusableInTouchMode(true);
            thisView.requestFocus();
            thisView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        showExitSessionDialog();
                        return true;
                    }

                    return false;
                }
            });

            listViewRecordedGames = (ListView) thisView.findViewById(R.id.listViewRecordedGames);
            listViewRecordedGames.setOnItemClickListener(this);

            glaa = new GameListArrayAdapter(this.getActivity(), R.layout.game_list_item, recordedGameList);
            listViewRecordedGames.setAdapter(glaa);

            /*Game fakeGame = new Game();
            Player whitePlayer = new Player();
            Player blackPlayer = new Player();
            whitePlayer.setFirstName("Oliver");
            whitePlayer.setLastName("Boswell");
            blackPlayer.setFirstName("Ringo");
            blackPlayer.setLastName("Starr");
            fakeGame.setWhitePlayer(whitePlayer);
            fakeGame.setBlackPlayer(blackPlayer);
            fakeGame.setResult(Game.GAME_RESULT_WHITE_WIN);
            Calendar calendar = Calendar.getInstance();
            fakeGame.setDate(calendar.getTime());
            this.recordedGameList.add(fakeGame);
            this.recordedGameList.add(fakeGame);*/
        }

        return thisView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO: Have this send a message to the server to remove the game.
        //this.recordedGameList.remove(position);
    }

    private class GameListArrayAdapter extends BaseAdapter {

        Context context = null;
        private List<Game> list;

        public GameListArrayAdapter(Context context, int textViewResourceId, List<Game> objects) {
            this.context = context;
            this.list = objects;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            TextView dateTime = null;
            TextView whiteName = null;
            TextView blackName = null;
            View whiteResultDot = null;
            View blackResultDot = null;

            Button btnRemoveButton = null;

            if(convertView == null) {
                LayoutInflater li = LayoutInflater.from(context);
                view = li.inflate(R.layout.game_list_item, parent, false);
            } else {
                view = convertView;
            }

            Game thisGame = list.get(position);

            dateTime = (TextView)view.findViewById(R.id.date_time);
            whiteName = (TextView)view.findViewById(R.id.white_name);
            blackName = (TextView)view.findViewById(R.id.black_name);
            whiteResultDot = view.findViewById(R.id.white_result_dot);
            blackResultDot = view.findViewById(R.id.black_result_dot);
            btnRemoveButton = (Button)view.findViewById(R.id.button_remove_game);
            btnRemoveButton.setTag(position);
            btnRemoveButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    HomeFragment.this.removeButtonClicked = ((Integer) v.getTag()).intValue();

                    // TODO: tell the server that we removed the game
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeFragment.this.getActivity());
                    builder.setMessage("Are you sure you want to erase this game?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            HomeFragment.this.recordedGameList.remove(HomeFragment.this.removeButtonClicked);
                            glaa.notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog

                        }
                    });

                    builder.show();

                }
            });

            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            dateTime.setText(df.format(thisGame.getDate()));

            whiteName.setText(thisGame.getWhitePlayer().getFirstName() + " " + thisGame.getWhitePlayer().getLastName());
            blackName.setText(thisGame.getBlackPlayer().getFirstName() + " " + thisGame.getBlackPlayer().getLastName());

            if (thisGame.getResult() == Game.GAME_RESULT_WHITE_WIN) {
                whiteResultDot.setBackground(getResources().getDrawable(R.drawable.win_background));
                blackResultDot.setBackground(getResources().getDrawable(R.drawable.lose_background));
            }
            else if (thisGame.getResult() == Game.GAME_RESULT_BLACK_WIN) {
                whiteResultDot.setBackground(getResources().getDrawable(R.drawable.lose_background));
                blackResultDot.setBackground(getResources().getDrawable(R.drawable.win_background));
            }
            else {
                whiteResultDot.setBackground(getResources().getDrawable(R.drawable.draw_background));
                blackResultDot.setBackground(getResources().getDrawable(R.drawable.draw_background));
            }

            return view;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    private void showExitSessionDialog() {
        if (endDialog == null || endDialog.isShowing() != true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeFragment.this.getActivity());
            builder.setMessage("Are you sure you want to end this session?");
            builder.setPositiveButton("End Session", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    HomeFragment.this.mListener.onCancelSession();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog

                }
            });

            endDialog = builder.create();
            endDialog.show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        thisView.requestFocus();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v == btnRecordNewGame)
            mListener.onRecordNewGameClicked();

        /*else if (v == btnEditGame) {

        }
        else if (v == btnRemoveGame) {

        }*/
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onRecordNewGameClicked();
        public void onCancelSession();
    }

    public void addGameResult(Game gameResult) {
        recordedGameList.add(gameResult);
        glaa.notifyDataSetChanged();
    }
}
