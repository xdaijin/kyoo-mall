<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const isRegister = ref(false)
const loading = ref(false)
const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  nickname: '',
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 32, message: '长度需在 3-32 之间', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 64, message: '长度需在 6-64 之间', trigger: 'blur' },
  ],
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = isRegister.value
      ? await register(form)
      : await login({ username: form.username, password: form.password })
    authStore.setAuth(data)
    ElMessage.success(isRegister.value ? '注册成功' : '登录成功')
    router.push(route.query.redirect || '/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2 class="title">Kyoo Mall {{ isRegister ? '注册' : '登录' }}</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item v-if="isRegister" label="昵称">
          <el-input v-model="form.nickname" placeholder="选填，默认同用户名" />
        </el-form-item>
        <el-button type="primary" :loading="loading" class="submit-btn" @click="submit">
          {{ isRegister ? '注册并登录' : '登录' }}
        </el-button>
      </el-form>
      <div class="switch">
        <el-link type="primary" @click="isRegister = !isRegister">
          {{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}
        </el-link>
      </div>
      <el-text class="hint" type="info" size="small">内置演示账号：admin / 123456</el-text>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.login-card {
  width: 380px;
}

.title {
  margin: 0 0 24px;
  text-align: center;
}

.submit-btn {
  width: 100%;
}

.switch {
  margin-top: 12px;
  text-align: center;
}

.hint {
  display: block;
  margin-top: 8px;
  text-align: center;
}
</style>
