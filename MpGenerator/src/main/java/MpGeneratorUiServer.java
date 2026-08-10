import com.github.davidfantasy.mybatisplus.generatorui.GeneratorConfig;
import com.github.davidfantasy.mybatisplus.generatorui.MybatisPlusToolsApplication;

/**
 * <p>
 * 代码生成器
 * @author chenjia
 * </p>
 */
public class MpGeneratorUiServer {

    public static void main(String[] args) {
        // 注意：连接信息请按本机环境修改；仓库内禁止提交真实口令与内网地址
        GeneratorConfig config = GeneratorConfig.builder().jdbcUrl("jdbc:mysql://127.0.0.1:3306/task_app?useUnicode=true&characterEncoding=utf-8")
                .userName("root")
                .password("") // 本地库密码请自行在运行前设置，勿留真实口令
                .port(9999)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .basePackage("com.nrec.service.app")
                .outputProjectPathRoot("./app")
                .build();

        String docUrl = "http://127.0.0.1:9999/";
		System.out.println("********************************************服务相关地址********************************************");
		System.out.printf(
				"代码生成UI界面地址: \t\t%s\n"
				, docUrl
		);
		System.out.println("********************************************服务相关地址********************************************");
        MybatisPlusToolsApplication.run(config);
    }

}