/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.kotox.securityshowcase.testutils

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import org.junit.runner.Description
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * A Junit rule that registers Architecture Components' background threads as an Espresso idling
 * resource.
 */
class TaskExecutorWithIdlingResourceRule : CountingTaskExecutorRule() {
	// give it a unique id to workaround an espresso bug where you cannot register/unregister
	// an idling resource w/ the same name.
	private val id = UUID.randomUUID().toString()
	private val idlingResource: IdlingResource = object : IdlingResource {
		override fun getName(): String {
			return "architecture components idling resource $id"
		}

		override fun isIdleNow(): Boolean {
			return this@TaskExecutorWithIdlingResourceRule.isIdle
		}

		override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
			callbacks.add(callback)
		}
	}
	private val callbacks = CopyOnWriteArrayList<IdlingResource.ResourceCallback>()
	override fun starting(description: Description?) {
		IdlingRegistry.getInstance().register(idlingResource)
		super.starting(description)
	}

	override fun finished(description: Description?) {
		drainTasks(10, TimeUnit.SECONDS)
		callbacks.clear()
		IdlingRegistry.getInstance().unregister(idlingResource)
		super.finished(description)
	}

	override fun onIdle() {
		super.onIdle()
		for (callback in callbacks) {
			callback.onTransitionToIdle()
		}
	}
}
