<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { pageProducts, createProduct } from '@/api/product'

const loading = ref(false)
const products = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10 })

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const form = reactive({ name: '', description: '', price: 0, stock: 0 })

const formRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const page = await pageProducts(query)
    products.value = page.records
    total.value = page.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  Object.assign(form, { name: '', description: '', price: 0, stock: 0 })
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    await createProduct(form)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div>
    <div class="toolbar">
      <h3>商品列表</h3>
      <el-button type="primary" @click="openCreate">新增商品</el-button>
    </div>

    <el-table v-loading="loading" :data="products" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="price" label="价格" width="120">
        <template #default="{ row }">¥ {{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="200" />
    </el-table>

    <el-pagination
      v-model:current-page="query.current"
      v-model:page-size="query.size"
      :total="total"
      layout="total, prev, pager, next"
      class="pagination"
      @current-change="loadData"
    />

    <el-dialog v-model="dialogVisible" title="新增商品" width="480px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.toolbar h3 {
  margin: 0;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
