package edu.utap.DamiKitz.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import edu.utap.DamiKitz.R

// Custom swipe-to-delete functionality for RecyclerView items
abstract class SwipeToDelete(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    // Icon for delete action
    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_white)
    private val iconWidth = deleteIcon!!.intrinsicWidth
    private val iconHeight = deleteIcon!!.intrinsicHeight

    // Background color and drawable for delete action
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#FF0000")

    // Paint to clear canvas
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    // Prevent swipe-to-delete for a specific item position
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (viewHolder.adapterPosition == 10) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    // Customize appearance during swipe action
    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw delete background
        background.color = backgroundColor
        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        background.draw(c)

        // Draw delete icon
        val deleteIconMargin = (itemHeight - iconHeight) / 2
        val deleteIconTop = itemView.top + (itemHeight - iconHeight) / 2
        val deleteIconBottom = deleteIconTop + iconHeight
        val deleteIconLeft = itemView.right - deleteIconMargin - iconWidth
        val deleteIconRight = itemView.right - deleteIconMargin

        deleteIcon!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteIcon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    // Clear canvas area
    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}
