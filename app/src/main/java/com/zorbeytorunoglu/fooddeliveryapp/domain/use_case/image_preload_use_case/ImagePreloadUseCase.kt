package com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.image_preload_use_case

import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zorbeytorunoglu.fooddeliveryapp.common.Constants
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Category
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class ImagePreloadUseCase @Inject constructor() {

    suspend operator fun invoke(categories: List<Category>, glide: RequestManager) {

        val deferredPreloads = mutableListOf<CompletableDeferred<Unit>>()

        categories.forEach {
            it.foodList.forEach { food ->
                val deferred = CompletableDeferred<Unit>()

                withContext(Dispatchers.IO) {
                    glide.load("${Constants.BASE_URL}${Constants.API_URL}${Constants.GET_FOOD_IMAGE_URL}${food.imageName}")
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable?>,
                                isFirstResource: Boolean
                            ): Boolean {
                                deferred.completeExceptionally(IOException("Image could not be loaded. ${e?.localizedMessage}"))
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any,
                                target: Target<Drawable?>?,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                deferred.complete(Unit)
                                return true
                            }
                        })
                        .preload()
                }

                deferredPreloads.add(deferred)
            }

        }

        deferredPreloads.awaitAll()

    }

}