package com.acnteam.crashybird.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.acnteam.crashybird.R

sealed class BackgroundMusic(
    val name: String,
    val author: String,
    @RawRes val music: Int,
    @DrawableRes val image: Int,
) {
    companion object {
        fun getAlls() = listOf(
            BacPhan,
            HongNhan,
            SongGio,
            ThienLyOi,
            DoToc1,
            DoToc2,
            RapBo,
            ThangDien,
        )

        fun getByName(name: String) = when (name) {
            BacPhan.name -> BacPhan
            HongNhan.name -> HongNhan
            SongGio.name -> SongGio
            ThienLyOi.name -> ThienLyOi
            DoToc1.name -> DoToc1
            DoToc2.name -> DoToc2
            RapBo.name -> RapBo
            ThangDien.name -> ThangDien
            else -> BacPhan
        }
    }

    // Jack
    data object BacPhan :
        BackgroundMusic("Bạc Phận", "Jack & K-ICM", R.raw.bac_phan, R.drawable.bac_phan)

    data object HongNhan :
        BackgroundMusic("Hồng Nhan", "Jack & K-ICM", R.raw.hong_nhan, R.drawable.hong_nhan)

    data object SongGio :
        BackgroundMusic("Sóng Gió", "Jack & K-ICM", R.raw.song_gio, R.drawable.song_gio)

    data object ThienLyOi :
        BackgroundMusic("Thiên Lý Ơi", "Jack", R.raw.thienlyoi, R.drawable.thien_ly_oi)

    // Do i i
    data object DoToc1 : BackgroundMusic("Độ Tộc 1", "Phúc Du", R.raw.toc_do, R.drawable.do_toc_1)
    data object DoToc2 :
        BackgroundMusic("Độ Tộc 2", "Phúc Du, Pháo & Độ Mixi", R.raw.toc_do_2, R.drawable.do_toc_2)

    data object RapBo :
        BackgroundMusic("Rap Bợ", "Quang Nịnh Thần", R.raw.rap_bo, R.drawable.rap_bo)

    // Virus
    data object ThangDien :
        BackgroundMusic(
            "Thằng Điên",
            "Phương Ly, JustaTee & Virus",
            R.raw.thang_dien,
            R.drawable.thang_dien
        )
}