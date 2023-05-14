<template>
  <div>
<!--搜索模块-->
    <div style="margin: 10px 0">
<!--      按名称搜索-->
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="name"></el-input>
<!--      点击搜索，更新显示内容-->
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
<!--      重置搜索内容，并更新显示-->
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">

<!--      文件上传模块-->
<!--      需要定义文件上传成功的回调-->
      <el-upload :action="'http://' + serverIp + ':9090/file/upload'" :show-file-list="false"
                 :on-success="handleFileUploadSuccess" style="display: inline-block">
        <el-button type="primary" class="ml-5">上传文件 <i class="el-icon-top"></i></el-button>
      </el-upload>

<!--      删除确认模块，将删除模块（按钮）包含，并定义确认删除该触发的事件-->
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference"> 批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>

    </div>
<!--    列表模块-->
<!--    要和data中的tableData对象数组绑定，列表项的prop属性、scope.row.xxx，均对应tableData中的属性，-->
<!--    当selection列表项被选中时，调用handlerSelectionChange函数，-->
    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
              @selection-change="handleSelectionChange">
<!--      实现批量删除的复选框-->
      <el-table-column type="selection" width="55"></el-table-column>

      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="文件名称"></el-table-column>
      <el-table-column prop="type" label="文件类型"></el-table-column>
      <el-table-column prop="size" label="文件大小(kb)"></el-table-column>

      <el-table-column label="预览">
<!--        列表项不是简单的输入框时-->
        <template slot-scope="scope">
          <el-button type="primary" @click="preview(scope.row.url)">预览</el-button>
        </template>
      </el-table-column>

      <el-table-column label="下载">
        <template slot-scope="scope">
          <el-button type="primary" @click="download(scope.row.url)">下载</el-button>
        </template>
      </el-table-column>

      <el-table-column label="启用">
        <template slot-scope="scope">
<!--          开关样式-->
          <el-switch v-model="scope.row.enable" active-color="#13ce66" inactive-color="#ccc"
                     @change="changeEnable(scope.row)"></el-switch>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">

          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗？"
              @confirm="del(scope.row.id)"
          >
            <el-button type="danger" slot="reference">删除 <i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>

        </template>
      </el-table-column>

    </el-table>

<!--    -->
    <div style="padding: 10px 0">
<!--      分页组件-->
<!--      绑定当前页面，页面数据条数，可选数据条数-->
<!--      指定当前页面改变时的回调：(参数：选择的页数，逻辑：更改存当前页数的变量，并重新像后端请求数据刷新页面)-->
<!--      指定当前页面条数改变时的回调：(参数：选择的数据条数，)-->
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[2, 5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

  </div>
</template>

<script>
import {serverIp} from "../../public/config";

export default {
  name: "File",
  data() {
    return {
      serverIp: serverIp,
      //用于接收后端传来的数据，并用于前端列表项的展示
      tableData: [],
      name: '',
      multipleSelection: [],
      pageNum: 1,
      pageSize: 10,
      total: 0
    }
  },
  created() {
    this.load()
  },
  methods: {
    //向后端请求对应的数据（按页面可显示的数据条数，当前页面，查询的关键字请求）
    load() {
      this.request.get("/file/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {

        this.tableData = res.data.records
        this.total = res.data.total

      })
    },
    changeEnable(row) {
      this.request.post("/file/update", row).then(res => {
        if (res.code === '200') {
          this.$message.success("操作成功")
        }
      })
    },
    del(id) {
      this.request.delete("/file/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },

    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/file/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.name = ""
      this.load()
    },
    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    },
    handleFileUploadSuccess(res) {
      console.log(res)
      this.$message.success("上传成功")
      this.load()
    },
    download(url) {
      window.open(url)
    },
    preview(url) {
      //打开新页面用于展示图片
      window.open('https://file.keking.cn/onlinePreview?url=' + encodeURIComponent(window.btoa((url))))
    },
  }
}
</script>

<style scoped>

</style>
