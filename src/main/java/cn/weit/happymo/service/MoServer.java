package cn.weit.happymo.service;


import cn.weit.happymo.context.MoMoContext;
import cn.weit.happymo.session.SessionManger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author weitong
 */
@Slf4j
@NoArgsConstructor
public class MoServer {
	private String ip;
	private int port;
	private ServerBootstrap bootstrap;
	private MoMoContext moMoContext;
	private Channel channel;
	private EventLoopGroup boss;
	private EventLoopGroup worker;
	private int bossNum = 1;
	private int workerNum = 1;

	public MoServer withHost(String ip, int port) {
		this.ip = ip;
		this.port = port;
		return this;
	}

	public MoServer withBossNum(int num) {
		this.bossNum = num;
		return this;
	}

	public MoServer withWorkerNum(int num) {
		this.workerNum = num;
		return this;
	}

	public void start() {
		this.moMoContext = new MoMoContext();
		SessionManger.Instance().init(workerNum);
		boss = new NioEventLoopGroup(bossNum);
		worker = new NioEventLoopGroup(workerNum);
		try {
			bootstrap = new ServerBootstrap();
			bootstrap.group(boss, worker)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel sc) throws Exception {
							ChannelPipeline cp = sc.pipeline();
							cp.addLast("timeout", new ReadTimeoutHandler(10));
							cp.addLast("codec", new HttpServerCodec());
							cp.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
							cp.addLast("myHandler", new MoHandler(moMoContext));
						}
					});
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_REUSEADDR, true)
					.option(ChannelOption.SO_BACKLOG, 100);
			channel = bootstrap.bind(ip, port).channel();
			log.info("MoServer start");
			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			log.error("MoServer Error!", e);
			stop();
		}
	}

	public void stop() {
		if (channel.isOpen()) {
			channel.close();
		}
		boss.shutdownGracefully();
		worker.shutdownGracefully();
		log.info("MoServer stop");
	}
}
