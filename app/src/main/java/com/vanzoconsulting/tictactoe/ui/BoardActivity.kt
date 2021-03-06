package com.vanzoconsulting.tictactoe.ui

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.core.text.HtmlCompat.fromHtml
import androidx.core.view.children
import com.vanzoconsulting.domain.Board
import com.vanzoconsulting.domain.Player
import com.vanzoconsulting.domain.Player.O
import com.vanzoconsulting.domain.Player.X
import com.vanzoconsulting.domain.label
import com.vanzoconsulting.tictactoe.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_board.*
import javax.inject.Inject

class BoardActivity: DaggerAppCompatActivity(), BoardContract.View {

    @Inject
    lateinit var presenter: BoardContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_board)
        fab_restart.hide()

        presenter.view = this
        presenter.loadBoard()
    }

    override fun renderBoard(board: Board) {
        board_grid.children.filterIsInstance(TextView::class.java).forEachIndexed { index, view ->
            view.text = board[index]?.label()
            view.setTextColor(getColor(this,
                if (board[index] == O)
                    R.color.colorPlayer2
                else
                    R.color.colorPlayer
            ))

            if (view.text.isEmpty()) {
                view.isClickable = true
                view.setOnClickListener {
                    presenter.makeMove(board, index)
                }
            } else {
                view.isClickable = false
                view.setOnClickListener(null)
            }
        }
    }

    override fun showWinner(player: Player) = showMessage(
        if (player == X)
            R.string.board_message_winner_x
        else
            R.string.board_message_winner_o
    )

    override fun showDraw() = showMessage(R.string.board_message_draw)

    private fun showMessage(@StringRes stringRes: Int) {
        tv_message.visibility = VISIBLE
        tv_message.text = fromHtml(getString(stringRes), FROM_HTML_MODE_COMPACT)
        fab_restart.show()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onRestartClick(v: View) {
        presenter.loadBoard()
    }

    override fun reset() {
        fab_restart.hide()
        tv_message.visibility = GONE
        renderBoard(Board())
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.view = null
    }
}
